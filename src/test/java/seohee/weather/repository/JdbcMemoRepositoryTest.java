package seohee.weather.repository;

import org.assertj.core.condition.Negative;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import seohee.weather.domain.Memo;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional  // DB에 반영되지 않도록 하기 위함 (테스트 후 원상복구)
class JdbcMemoRepositoryTest {

    @Autowired
    JdbcMemoRepository jdbcMemoRepository;

    @Test
    void insertMemoTest() {
        // given
        Memo newMemo = new Memo(1, "InsertMemoTest");

        // when
        jdbcMemoRepository.save(newMemo);

        // then
        Optional<Memo> result = jdbcMemoRepository.findById(1);
        assertEquals(result.get().getText(), "InsertMemoTest");
    }

    @Test
    void findAllMemoTest() {
        // given
        Memo memo1 = new Memo(1, "memo1");
        Memo memo2 = new Memo(2, "memo2");
        jdbcMemoRepository.save(memo1);
        jdbcMemoRepository.save(memo2);

        // when
        List<Memo> memoList = jdbcMemoRepository.findAll();

        // then
        assertNotNull(memoList);
    }



}