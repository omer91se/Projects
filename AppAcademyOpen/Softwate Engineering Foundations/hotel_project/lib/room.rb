class Room

  def initialize(capacity)
    @capacity = capacity
    @occupants = []
  end
  def capacity
    @capacity
  end

  def occupants
    @occupants
  end

  def full?
    return true if @occupants.length == @capacity
    
    false
  end

  def available_space
    @capacity - @occupants.length
  end

  def add_occupant(name)
    unless full?
      @occupants << name
      return true
    end
    false
  end


end
