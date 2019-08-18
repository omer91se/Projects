require "employee"
require "byebug"

class Startup
  attr_reader :name, :funding, :salaries, :employees

  def initialize(name, funding, salaries)
    @name = name
    @funding = funding
    @salaries = salaries
    @employees = []
  end

  def valid_title?(title)
    return true if @salaries.key?(title)

    false
  end

  def >(other)
    return true if @funding > other.funding

    false
  end

  def hire(employee_name, title)
    if valid_title?(title)
      @employees << Employee.new(employee_name, title)
    else
      raise "We Dont Hire #{title}!!!"
    end
  end

  def size
    @employees.length
  end

  def pay_employee(emp)
    salary = @salaries[emp.title]
    if @funding > salary
      emp.pay(salary)
      @funding -= salary
    else
      raise "Not enough money"
    end
  end

  def payday
    @employees.each { |emp| pay_employee(emp) }
  end

  def average_salary
    sum = 0
    @employees.map { |emp| @salaries[emp.title] }.sum / @employees.length.to_f
  end

  def close
    @employees = []
    @funding = 0
  end

  def acquire(other)
    @funding += other.funding
    other.salaries.each do |title, salary|
      @salaries[title] = salary unless @salaries.key?(title)
    end
    @employees.concat other.employees
    other.close
  end
end
