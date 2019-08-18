# Write a method, least_common_multiple, that takes in two numbers and returns the smallest number that is a mutiple 
# of both of the given numbers
def least_common_multiple(num_1, num_2)
  max_num = [num_1, num_2].max
  (max_num..(num_1 * num_2)).each do |lcm|
    return lcm if (lcm % num_1).zero? && (lcm % num_2).zero?
  end
end


# Write a method, most_frequent_bigram, that takes in a string and returns the two adjacent letters that appear the
# most in the string.
def most_frequent_bigram(str)
  bigrams = Hash.new(0)
  i = 0
  while i < str.length - 1
    bigrams[str[i..i + 1]] += 1
    i += 1
  end
  max_big_val = 0
  biggest_big = str[0..1]
  bigrams.each do |key, val|
    if max_big_val < val
      max_big_val = val
      biggest_big = key
    end
  end
  biggest_big
end


class Hash
  # Write a method, Hash#inverse, that returns a new hash where the key-value pairs are swapped
  def inverse
    hash = {}
    each { |key, val| hash[val] = key }
    hash
  end
end


class Array
  # Write a method, Array#pair_sum_count, that takes in a target number returns the number of pairs of elements that sum to the given target
  def pair_sum_count(num)
    pair_count = 0
    (0...length).each do |i|
      ((i + 1)...length).each do |j|
        pair_count += 1 if self[i] + self[j] == num
      end
    end
    pair_count
  end


  # Write a method, Array#bubble_sort, that takes in an optional proc argument.
  # When given a proc, the method should sort the array according to the proc.
  # When no proc is given, the method should sort the array in increasing order.
  def bubble_sort(&prc)
    prc ||= ->(x, y) { x <=> y }

    sorted = false

    until sorted
      sorted = true
      (0...length - 1).each do |i|
        if prc.call(self[i], self[i + 1]) == 1

          sorted = false
          self[i], self[i + 1] = self[i + 1], self[i]
        end
      end
    end
    self
  end

end

