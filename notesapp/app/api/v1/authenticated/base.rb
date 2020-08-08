module V1
  module Authenticated
    class Base < Grape::API
      helpers V1::Helpers::Authentication
      helpers V1::Helpers::Utils

      before do
        authenticate!
      end

      mount V1::Authenticated::Notes
      mount V1::Authenticated::User
    end
  end
end
