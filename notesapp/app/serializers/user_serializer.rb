class UserSerializer
  include FastJsonapi::ObjectSerializer
  attributes :name, :username, :email, :auth_token, :phone_number
end
