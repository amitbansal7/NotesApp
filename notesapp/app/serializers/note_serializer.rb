class NoteSerializer
  include FastJsonapi::ObjectSerializer
  attributes :title, :text, :public_url

  attribute :note_id do |note|
    note.id
  end

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
