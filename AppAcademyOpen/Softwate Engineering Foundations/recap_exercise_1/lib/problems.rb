# Write a method, all_vowel_pairs, that takes in an array of words and returns all pairs of words
# that contain every vowel. Vowels are the letters a, e, i, o, u. A pair should have its two words
# in the same order as the original array. 
#
# Example:
#
# all_vowel_pairs(["goat", "action", "tear", "impromptu", "tired", "europe"])   # => ["action europe", "tear impromptu"]
def all_vowel_pairs(words)
  pair = ''
  pairs = []
  i = 0
  while i < words.length - 1
    j = i + 1
    while j < words.length
      pairs << "#{words[i]} #{words[j]}" if all_vowel?(words[i], words[j])
      j += 1
    end
    i += 1
  end
  pairs
end

def all_vowel?(word1, word2)
  vowels_map = {'a' => false,
                'e' => false,
                'i' => false,
                'o' => false,
                'u' => false}
  word1.each_char { |char| vowels_map[char] = true if vowels_map.key?(char) }
  word2.each_char { |char| vowels_map[char] = true if vowels_map.key?(char) }
  vowels_map.values.all?
end

# Write a method, composite?, that takes in a number and returns a boolean indicating if the number
# has factors besides 1 and itself
#
# Example:
#
# composite?(9)     # => true
# composite?(13)    # => false
def composite?(num)
  (2...num).each { |factor| return true if (num % factor).zero? }
  false
end


# A bigram is a string containing two letters.
# Write a method, find_bigrams, that takes in a string and an array of bigrams.
# The method should return an array containing all bigrams found in the string.
# The found bigrams should be returned in the the order they appear in the original array.
#
# Examples:
#
# find_bigrams("the theater is empty", ["cy", "em", "ty", "ea", "oo"])  # => ["em", "ty", "ea"]
# find_bigrams("to the moon and back", ["ck", "oo", "ha", "at"])        # => ["ck", "oo"]
def find_bigrams(str, bigrams)

  bigs = []
  bigrams.each do |big|
    bigs << big if str.index(big)
  end

  bigs
end

class Hash
  # Write a method, Hash#my_select, that takes in an optional proc argument
  # The method should return a new hash containing the key-value pairs that return
  # true when passed into the proc.
  # If no proc is given, then return a new hash containing the pairs where the key is equal to the value.
  #
  # Examples:
  #
  # hash_1 = {x: 7, y: 1, z: 8}
  # hash_1.my_select { |k, v| v.odd? }          # => {x: 7, y: 1}
  #
  # hash_2 = {4=>4, 10=>11, 12=>3, 5=>6, 7=>8}
  # hash_2.my_select { |k, v| k + 1 == v }      # => {10=>11, 5=>6, 7=>8})
  # hash_2.my_select                            # => {4=>4}
  def my_select(&prc)
    hash = if !prc.nil?
             select { |key, val| prc.call(key, val) }
           else
             select { |key, val| key == val }
           end
    hash
  end

end

class String
  # Write a method, String#substrings, that takes in a optional length argument
  # The method should return an array of the substrings that have the given length.
  # If no length is given, return all substrings.
  #
  # Examples:
  #
  # "cats".substrings     # => ["c", "ca", "cat", "cats", "a", "at", "ats", "t", "ts", "s"]
  # "cats".substrings(2)  # => ["ca", "at", "ts"]
  def substrings(length = nil)
    arr = []
    if length.nil?
      (1..self.length).each { |i| arr << substring_size(i) }
      arr.flatten!
    else
      arr << substring_size(length)
      arr.flatten!
    end
    arr
  end




  # Write a method, String#caesar_cipher, that takes in an a number.
  # The method should return a new string where each char of the original string is shifted
  # the given number of times in the alphabet.
  #
  # Examples:
  #
  # "apple".caesar_cipher(1)    #=> "bqqmf"
  # "bootcamp".caesar_cipher(2) #=> "dqqvecor"
  # "zebra".caesar_cipher(4)    #=> "difve"
  def caesar_cipher(num)
    alphabet = 'abcdefghijklmnopqrstuvwxyz'
    cipher = ''
    each_char do |char|
      index = (alphabet.index(char.downcase) + num) % 26
      cipher << alphabet[index]
    end
    cipher
  end

  private

  def substring_size(length)
    subsets = []
    (0..(self.length - length)).each do |i|
      subsets << self[i..(i + length - 1)]
    end
    subsets
  end

end
