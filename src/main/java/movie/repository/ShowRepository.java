package movie.repository;

import movie.model.Show;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowRepository extends JpaRepository<Show, Long> {
    Optional<Show> findById(@NotNull Long id);
}
