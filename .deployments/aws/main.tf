provider "aws" {
  region = "ap-southeast-1"
}

locals {
  project = "tea-mates"
  env     = "dev"
  name    = "${local.project}-${local.env}"
  region  = "ap-southeast-1"

  tags = {
    Project   = local.project
    CreatedBy = "terraform"
  }
}


