require 'roar'
require 'roar/json'

class Api < Grape::API
  default_format :json
  format :json
  prefix :api
  formatter :json, Grape::Formatter::Roar

  mount V1::Base
end
