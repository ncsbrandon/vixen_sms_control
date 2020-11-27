package status;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Root {
	@JsonProperty("State")
	public int state;
	@JsonProperty("Sequence")
	public Sequence sequence;
	@JsonProperty("Position")
	public String position;
	@JsonProperty("Message")
	public Object message;
	@JsonProperty("Details")
	public List<Object> details;
	@JsonProperty("IsSuccessful")
	public boolean isSuccessful;

	@Override
	public String toString() {
		return String.format("[%d][%s]", state, sequence.name);
	}
}