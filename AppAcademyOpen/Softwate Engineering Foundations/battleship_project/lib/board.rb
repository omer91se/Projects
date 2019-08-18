class Board

  attr_reader :size

  def initialize(n)
    @grid = Array.new(n) { |i| Array.new(n) { :N } }
    @size = n * n
  end

  def self.print_grid(grid)
    grid.each do |row|
      puts row.join(" ")
    end
  end

  def [](position)
    col, row = position
    @grid[col][row]
  end

  def []=(position, value)
    col, row = position
    @grid[col][row] = value
  end

  def num_ships
    @grid.flatten.count { |ele| ele == :S }
  end

  def attack(position)
    if self[position] == :S
      self[position] = :H
      puts 'you sunk my battleship!'
      true
    else
      self[position] = :X
      false
    end
  end

  def place_random_ships()
    total_ships = @size * 0.25
    while num_ships < total_ships
      rand_row = rand(0...@grid.length)
      rand_col = rand(0...@grid.length)
      pos = [rand_row, rand_col]
      self[pos] = :S
    end
  end

  def hidden_ships_grid
    arr = []
    @grid.each do |row|
      arr  << row.map do |ele|
        if ele == :S
          :N
        else
          ele
        end
      end
    end
    arr
  end

  def cheat
    Board.print_grid(@grid)
  end

  def print
    Board.print_grid(hidden_ships_grid)
  end

end

# b = Board.new(4)
# b.place_random_ships
# b.hidden_ships_grid
# Board.print_grid([[:S, :N][:X, :S]])

