locals {
  container_name = "tea-mates-service"
  container_port = 8080
  tg_name = "teamates-ecs"
}

module "ecs_cluster" {
  source = "terraform-aws-modules/ecs/aws//modules/cluster"
  name   = local.name

  # Capacity provider
  default_capacity_provider_strategy = {
    FARGATE = {
      weight = 50
      base   = 20
    }
    FARGATE_SPOT = {
      weight = 50
    }
  }

  tags = local.tags
}

################################################################################
# Service
################################################################################

module "ecs_service" {
  source      = "terraform-aws-modules/ecs/aws//modules/service"
  name        = "${local.name}-core-svc"
  cluster_arn = module.ecs_cluster.arn

  cpu    = 512
  memory = 1024

  # Enables ECS Exec
  enable_execute_command = true

  container_definitions = {
    (local.container_name) = {
      cpu       = 512
      memory    = 1024
      essential = true
      image     = "${var.ecr_registry}/teamates-service:latest"

      environment = [
        { name = "SPRING_PROFILES_ACTIVE", value = "aws" }
      ]

      port_mappings = [{
        containerPort = local.container_port
        hostPort      = local.container_port
        protocol      = "tcp"
      }]

      health_check = {
        command = ["CMD-SHELL", "curl -f http://localhost:8080/actuator/health || exit 1"]
      }

      enable_cloudwatch_logging = true
      readonly_root_filesystem  = false
    }
  }

  subnet_ids = module.vpc.private_subnets

  security_group_ingress_rules = {
    alb_8080 = {
      from_port                    = local.container_port
      to_port                      = local.container_port
      ip_protocol                  = "tcp"
      description                  = "Service port"
      referenced_security_group_id = module.alb.security_group_id
    }
  }
  security_group_egress_rules = {
    all = {
      cidr_ipv4   = "0.0.0.0/0"
      ip_protocol = "-1"
      from_port   = 0
      to_port     = 0
    }
  }

  load_balancer = {
    service = {
      target_group_arn = module.alb.target_groups[local.tg_name].arn
      container_name   = local.container_name
      container_port   = local.container_port
    }
  }

  tasks_iam_role_name        = "${local.name}-core-svc-task-role"
  tasks_iam_role_description = "IAM role for teamates-service ECS task"

  tasks_iam_role_statements = [
    {
      actions = [
        "ecr:GetAuthorizationToken",
        "ecr:BatchCheckLayerAvailability",
        "ecr:GetDownloadUrlForLayer",
        "ecr:BatchGetImage"
      ]
      resources = ["*"]
    },
    {
      actions = [
        "logs:CreateLogStream",
        "logs:PutLogEvents"
      ]
      resources = ["*"]
    }
  ]
  tags = local.tags
}

module "alb" {
  source = "terraform-aws-modules/alb/aws"
  name   = local.name

  load_balancer_type = "application"

  vpc_id  = module.vpc.vpc_id
  subnets = module.vpc.public_subnets

  enable_deletion_protection = false

  # Security Group
  security_group_ingress_rules = {
    all_http = {
      from_port   = 80
      to_port     = 80
      ip_protocol = "tcp"
      cidr_ipv4   = "0.0.0.0/0"
    }
  }

  security_group_egress_rules = {
    all = {
      ip_protocol = "-1"
      cidr_ipv4   = module.vpc.vpc_cidr_block
    }
  }

  listeners = {
    http = {
      port     = 80
      protocol = "HTTP"

      forward = {
        target_group_key = local.tg_name
      }
    }
  }

  target_groups = {
    (local.tg_name) = {
      name                              = local.tg_name
      backend_protocol                  = "HTTP"
      backend_port                      = local.container_port
      target_type                       = "ip"
      deregistration_delay              = 5
      load_balancing_cross_zone_enabled = true

      health_check = {
        enabled             = true
        healthy_threshold   = 3
        interval            = 20
        matcher             = "200"
        path                = "/actuator/health"
        port                = "traffic-port"
        protocol            = "HTTP"
        timeout             = 5
        unhealthy_threshold = 2
      }

      create_attachment = false # ECS will handle it
    }
  }

  tags = local.tags
}
