class Note < ApplicationRecord
  acts_as_paranoid

  belongs_to :user

  validates_presence_of :title, :user

  def make_public
    update(public_token: SecureRandom.hex(12)) if public_token.blank?
  end

  def public_url
    public_token
  end

  def make_private
    update(public_token: nil)
  end
end
