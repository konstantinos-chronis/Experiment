package interview;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Tickets {

	public record Ticket(int id, String customerName, String priority, String status, double hours) {
		
		public Ticket {
			if(id < 1 || hours < 0) {
				throw new IllegalArgumentException();
			}
		}
		
		public double slaHoursLimit() {
			
			return switch(this.priority()) {
				case String s when s.equals("Critical") -> {
					yield switch(this.status()) {
						case String str when str.equals("Open") -> 3;
						default -> 8;
					};
				}
				case String s when s.equals("High") -> 24; 
				case String s when s.equals("Medium") -> 72; 
				case String s when s.equals("Low") -> 168; 
				default -> -1;
			};
		}
		
		public boolean isBreachingSla() {
			return this.hours() > this.slaHoursLimit();
		}
		
		public static Optional<Ticket> findMostUrgentBreach(List<Ticket> tickets) {
			Optional<Ticket> breachingTicket = tickets.stream().
					filter(Ticket::isBreachingSla).
					max(Comparator.comparingDouble(Ticket::hours));
			
			return breachingTicket;
		}
	}
	
	public static void main(String [] args) {
		List<Ticket> tickets = List.of(
				new Ticket(1, "A", "Critical", "Open", 10),
				new Ticket(2, "B", "High", "Open", 10),
				new Ticket(3, "C", "Medium", "Open", 10),
				new Ticket(4, "D", "Low", "Open", 200),
				new Ticket(5, "E", "Critical", "Resolved", 10),
				new Ticket(6, "F", "Critical", "In_Progress", 10));
		
		System.out.println("Breaching tickets:" + tickets.stream().filter(Ticket::isBreachingSla).count());
		
		Map<String, List<Ticket>> groupByPriority = new HashMap<>();
		groupByPriority = tickets.stream().collect(Collectors.groupingBy((Ticket::priority)));
		
		System.out.println(groupByPriority);
		
		List<Ticket> criticalTickets = tickets.stream().
				filter(p -> p.priority().equals("Critical")).
				filter(s -> s.status().equals("Open")).
				sorted(Comparator.comparingDouble(Ticket::hours).reversed()).
				toList();
		
		System.out.println("CriticalTickets:" + criticalTickets);
		
	}
}
