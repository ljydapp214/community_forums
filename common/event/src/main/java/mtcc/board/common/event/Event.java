package mtcc.board.common.event;

import mtcc.board.common.serializer.DataSerializer;

public record Event<T extends EventPayload>(Long eventId, EventType type, T payload) {

	public static Event<EventPayload> of(
		Long eventId,
		EventType type,
		EventPayload payload
	) {
		return new Event<>(eventId, type, payload);
	}

	public static Event<EventPayload> fromJson(String json) {
		EventRaw eventRaw = DataSerializer.deserialize(json, EventRaw.class);
		if (eventRaw == null) {
			return null;
		}

		EventType eventType = EventType.from(eventRaw.type());
		return Event.of(
			eventRaw.eventId(),
			eventType,
			DataSerializer.deserialize(eventRaw.payload(), eventType.getPayloadClass())
		);
	}

	public String toJson() {
		return DataSerializer.serialize(this);
	}

	private record EventRaw(Long eventId, String type, Object payload) {
	}
}
