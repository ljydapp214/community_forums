package mtcc.board.common.outbox;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mtcc.board.common.outbox.entity.Outbox;

@Repository
public interface OutboxRepository extends JpaRepository<Outbox, Long> {
	List<Outbox> findAllByShardKeyAndCreatedAtLessThanEqualOrderByCreatedAtAsc(
		Long shardKey,
		LocalDateTime from,
		Pageable pageable
	);
}
