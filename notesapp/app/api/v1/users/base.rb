module V1
  module Users
    class Base < Grape::API
      helpers V1::Helpers::Authentication
      helpers V1::Helpers::Utils

      before do
        authenticate!
      end

      namespace :users do
        mount V1::Users::Notes
        mount V1::Users::User
      end
    end
  end
end
