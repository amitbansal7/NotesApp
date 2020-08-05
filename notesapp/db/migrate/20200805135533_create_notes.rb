class CreateNotes < ActiveRecord::Migration[6.0]
  def change
    create_table :notes do |t|
      t.string :title
      t.text :text
      t.references :user, index: true
      t.string :public_token, index: true
      t.datetime :deleted_at

      t.timestamps
    end
  end
end
