require_relative "room"

class Hotel
  def initialize(name, hash)
    @name = name
    @rooms = {}
    hash.each { |key, val| @rooms[key] = Room.new(val) }
  end

  def name
    @name.split.map(&:capitalize).join(' ')
  end

  def rooms
    @rooms
  end

  def room_exists?(room_name)
    return true if @rooms.include? room_name

    false
  end

  def check_in(name, room_name)
    if room_exists? room_name
      if @rooms[room_name].add_occupant name
        puts 'check in successful'
      else
        puts 'sorry, room is full'
      end
    else
      puts 'sorry, room does not exist'
    end
  end

  def has_vacancy?
    !rooms.values.all?(&:full?)
  end

  def list_rooms
    @rooms.each { |room_name, room| puts "#{room_name} #{room.available_space}" }
  end
end
