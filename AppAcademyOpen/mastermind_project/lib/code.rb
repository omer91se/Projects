class Code
  require 'byebug'
  attr_reader :pegs
  POSSIBLE_PEGS = {
      "R" => :red,
      "G" => :green,
      "B" => :blue,
      "Y" => :yellow
  }

  def self.valid_pegs?(arr)
    arr.all? { |ele| POSSIBLE_PEGS.include?(ele.upcase) }
  end

  def initialize(pegs)
    if Code.valid_pegs?(pegs)
      @pegs = pegs.map(&:upcase)
    else
      raise "bad pegs"
    end
  end

  def self.random(length)
    arr = []
    length.times { arr << POSSIBLE_PEGS.keys.sample }
    code = Code.new(arr)
  end

  def self.from_string(pegs_str)
    Code.new(pegs_str.split(''))
  end

  def [](index)
    @pegs[index]
  end

  def length
    @pegs.length
  end

  def num_exact_matches(other)
    count = 0
    other.pegs.each_with_index { |peg, i| count += 1 if @pegs[i] == peg }
    count
  end

  def num_near_matches(other)
    count = 0
    other.pegs.each_with_index do |peg, i|
      count += 1 if self[i] != peg && @pegs.include?(peg)
    end
    count
  end

  def ==(other)
    num_exact_matches(other) == length && other.length == length
  end

end
