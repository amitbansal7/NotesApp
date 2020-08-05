module V1
  module Open
    class User < Grape::API
      namespace :user do
        params do
          requires :email, type: String, allow_blank: false
          requires :password, type: String, allow_blank: false
          requires :username, type: String, allow_blank: false
          optional :phone_number, type: String, allow_blank: true
        end
        post 'signup' do
          user = ::User.create(permitted_params)
          if user.save
            render_success(message: 'User Created')
          else
            render_error(message: user.errors.full_messages.join(', '))
          end
        end

        params do
          requires :username_or_email, type: String, allow_blank: false
          requires :password, type: String, allow_blank: false
        end
        get 'signin' do
          user = ::User.where(email: permitted_params[:username_or_email])
                       .or(::User.where(username: permitted_params[:username_or_email])).first

          if user.present? && user.valid_password?(permitted_params[:password])
            render_success(message: 'Login successful', data: serialized_data(user))
          else
            render_error(message: 'Username/Email or password is invalid')
          end
        end

        params do
          requires :auth_token, type: String, allow_blank: false
        end
        get 'auth' do
          user = ::User.where(auth_token: permitted_params[:auth_token]).first

          if user.present?
            render_success(message: 'Authenticated', data: { authenticated: true, user: serialized_data(user) })
          else
            render_error(message: 'Not authenticated', data: { authenticated: false })
          end
        end
      end
    end
  end
end
