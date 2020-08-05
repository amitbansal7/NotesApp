class User < ApplicationRecord
  acts_as_paranoid
  # Include default devise modules. Others available are:
  # :confirmable, :lockable, :timeoutable, :trackable and :omniauthable
  devise :database_authenticatable, :registerable,
         :recoverable, :rememberable, :validatable

  phony_normalize :phone_number

  before_validation :ensure_auth_token

  before_update :update_auth_token, if: :email_or_password_or_phone_number_changed?

  # validations
  validates_plausible_phone :phone_number
  validates :email, format: { with: URI::MailTo::EMAIL_REGEXP }
  validates_presence_of :email, :username
  validates_uniqueness_of :email, :username

  has_many :notes

  private

  def ensure_auth_token
    self.auth_token = generate_authentication_token if auth_token.blank?
  end

  def generate_authentication_token
    loop do
      token = Devise.friendly_token
      break token unless User.where(auth_token: token).present?
    end
  end

  def update_auth_token
    self.auth_token = generate_authentication_token
  end

  def email_or_password_or_phone_number_changed?
    email_changed? || encrypted_password_changed? || phone_number_changed?
  end
end
