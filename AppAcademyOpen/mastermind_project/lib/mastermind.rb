require_relative "code"

class Mastermind

  def initialize(length)
    @secret_code = Code.random(length)
  end

  def print_matches(other)
    puts "Exact match: #{@secret_code.num_exact_matches(other)}"
    puts "Near match: #{@secret_code.num_near_matches(other)}"
  end

  def ask_user_for_guess
    puts 'Enter a code'
    code = Code.from_string gets.chomp
    print_matches(code)
    code == @secret_code
  end

end
