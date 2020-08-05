class NoteSerializer
  include FastJsonapi::ObjectSerializer
  attributes :id, :title, :text, :public_url

  attribute :username do |note|
    note.user.username
  end

  attribute :created_at do |note|
    note.created_at&.to_i
  end

  attribute :updated_at do |note|
    note.created_at&.to_i
  end
end
