package seohee.weather.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import seohee.weather.domain.DateWeather;
import seohee.weather.domain.Diary;
import seohee.weather.repository.DateWeatherRepository;
import seohee.weather.repository.DiaryRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@Transactional
class DiaryServiceTest {

    @Autowired
    DiaryService diaryService;
    @Autowired
    DiaryRepository diaryRepository;
    @Autowired
    DateWeatherRepository dateWeatherRepository;

    @Test
    void createDiaryTest() {
        // given
        LocalDate date = LocalDate.of(2025,1,1);
        String text = "일기 추가 테스트";

        DateWeather dateWeather = new DateWeather();
        dateWeather.setDate(date);
        dateWeather.setTemperature(20.0);
        dateWeather.setWeather("clear");
        dateWeather.setIcon("^^");
        dateWeatherRepository.save(dateWeather);

        // when
        diaryService.createDiary(date, text);

        // then
        Diary savedDiary = diaryRepository.getFirstByDate(date);
        assertEquals(text, savedDiary.getText());
        assertEquals(dateWeather.getWeather(), savedDiary.getWeather());
        assertEquals(dateWeather.getIcon(), savedDiary.getIcon());
    }
    
    @Test
    void readDiaryTest() {
        // given
        LocalDate date = LocalDate.of(2025,1,1);;
        String text = "단일 일기 조회 테스트";

        DateWeather dateWeather = new DateWeather();
        dateWeather.setDate(date);
        dateWeather.setTemperature(20.0);
        dateWeather.setWeather("clear");
        dateWeather.setIcon("^^");
        dateWeatherRepository.save(dateWeather);

        Diary diary = new Diary();
        diary.setDate(date);
        diary.setText(text);
        diary.setDateWeather(dateWeather);
        diaryRepository.save(diary);

        // when 
        List<Diary> result = diaryService.readDiary(date);

        // then
        assertEquals(date, result.get(0).getDate());
        assertEquals(text, result.get(0).getText());
    }

    @Test
    void readDiariesTest() {
        // given
        LocalDate date1 = LocalDate.of(2026,1,1);
        String text1 = "기간 일기 조회 테스트1";

        DateWeather dateWeather1 = new DateWeather();
        dateWeather1.setDate(date1);
        dateWeather1.setTemperature(20.0);
        dateWeather1.setWeather("clear");
        dateWeather1.setIcon("^^");
        dateWeatherRepository.save(dateWeather1);

        Diary diary1 = new Diary();
        diary1.setDate(date1);
        diary1.setText(text1);
        diary1.setDateWeather(dateWeather1);
        diaryRepository.save(diary1);

        LocalDate date2 = LocalDate.of(2026,10,1);
        String text2 = "기간 일기 조회 테스트2";

        DateWeather dateWeather2 = new DateWeather();
        dateWeather2.setDate(date2);
        dateWeather2.setTemperature(20.0);
        dateWeather2.setWeather("clear");
        dateWeather2.setIcon("^^");
        dateWeatherRepository.save(dateWeather2);

        Diary diary2 = new Diary();
        diary2.setDate(date2);
        diary2.setText(text2);
        diary2.setDateWeather(dateWeather2);
        diaryRepository.save(diary2);

        // when
        List<Diary> result = diaryService.readDiaries(
                LocalDate.of(2026,1,1),
                LocalDate.of(2026,10,30)
        );

        // then
        assertEquals(2, result.size());
    }

    @Test
    void updateDiaryTest() {
        // given
        LocalDate date = LocalDate.of(2025,1,1);;
        String text = "일기 수정 테스트";

        DateWeather dateWeather = new DateWeather();
        dateWeather.setDate(date);
        dateWeather.setTemperature(20.0);
        dateWeather.setWeather("clear");
        dateWeather.setIcon("^^");
        dateWeatherRepository.save(dateWeather);

        Diary diary = new Diary();
        diary.setDate(date);
        diary.setText(text);
        diary.setDateWeather(dateWeather);
        diaryRepository.save(diary);

        // when
        String newText = "수정 완료";
        diaryService.updateDiary(date, newText);

        // then
        Diary updatedDiary = diaryRepository.getFirstByDate(date);
        assertEquals(newText, updatedDiary.getText());
    }

    @Test
    void deleteDiaryTest() {
        // given
        LocalDate date = LocalDate.of(2025,1,1);;
        String text = "일기 삭제 테스트";

        DateWeather dateWeather = new DateWeather();
        dateWeather.setDate(date);
        dateWeather.setTemperature(20.0);
        dateWeather.setWeather("clear");
        dateWeather.setIcon("^^");
        dateWeatherRepository.save(dateWeather);

        Diary diary = new Diary();
        diary.setDate(date);
        diary.setText(text);
        diary.setDateWeather(dateWeather);
        diaryRepository.save(diary);

        // when
        diaryService.deleteDiary(date);

        // then
        assertNull(diaryRepository.getFirstByDate(date));
    }
}