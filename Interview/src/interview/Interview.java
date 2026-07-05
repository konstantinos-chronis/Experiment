package interview;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Interview {

	public record Employee(String name, String department, int yearsofExp, double baseSalary) {
		
		public Employee {
			
			if(yearsofExp < 0 || baseSalary <= 0) {
				throw new IllegalArgumentException();
			}
			
		}
		
		public double calculateBonusRate() {
			
			return switch(this.department()) {
				
				case String str when str.equals("Engineering") -> {
					Integer years = this.yearsofExp();
					
					yield switch(years) {
						case Integer y when y >= 5 -> 0.15; 
						default -> 0.10;
					};
				}
				
				case String str when str.equals("Support") -> {
					Integer years = this.yearsofExp();
					
					yield switch(years) {
						case Integer y when y >= 3 -> 0.08;
						default -> 0.05;
					};
				}
				
				case String str when str.equals("Sales") -> 0.12;
				
				default -> 0.0;
				
			};
		}
		
		public double calculateBonus() {
			return this.baseSalary() * calculateBonusRate();
		}
		
		public static Optional<Employee> findEmployeeByName(List<Employee> employees, String name) {
			Optional<Employee> emp = employees.stream().
					filter(s -> s.name().equals(name)).
					findFirst();
			
			return emp;
		}
		
		public static String getDepartmentOrDefault(List<Employee> employees, String name) {
			return findEmployeeByName(employees, name).
					map(Employee::department).
					orElse("Unknown");
			
		}
	}
	
	public static void main(String [] args) {
		List<Employee> employees = List.of(
				new Employee("A", "Engineering", 6, 800),
				new Employee("B", "Engineering", 3, 1000),
				new Employee("C", "Sales", 5, 1000),
				new Employee("D", "Support", 3, 1000),
				new Employee("E", "Support", 10, 1000)
				);
		
		double totalBonus = employees.stream().mapToDouble(Employee::calculateBonus).sum();
		
		Optional<Employee> topEarner = employees.stream().
				max(Comparator.comparingDouble(Employee::calculateBonus));
		
		Map<String, List<Employee>> byDepartment = employees.stream().collect(Collectors.groupingBy(Employee::department));
		
		
		Map<String, Double> avgYearsByDept = employees.stream().
				collect(Collectors.groupingBy(
						Employee::department,
						Collectors.averagingInt(Employee::yearsofExp)));
		
		
//		System.out.println("Total Bonus :" + totalBonus);
//		System.out.println("Top Earner:" + topEarner);
//		System.out.println("Departments: " + byDepartment);
//		System.out.println("Average:" + avgYearsByDept);
		
		List<String> names = employees.stream().
				filter(y -> y.yearsofExp > 5).
				map(Employee::name).
				collect(Collectors.toList());
		
		System.out.println(names);
		
		Employee.findEmployeeByName(employees, "A").ifPresentOrElse(
				e -> System.out.println("Exists"),
				() -> System.out.println("Not found"));
		
		System.out.println("Dept:" + Employee.getDepartmentOrDefault(employees, "A"));
		
		
		System.out.println();
		
		employees.stream().filter(bs -> bs.baseSalary < 900).
			collect(Collectors.groupingBy(Employee::department));
	}
}
