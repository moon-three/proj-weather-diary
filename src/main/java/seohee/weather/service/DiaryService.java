package seohee.weather.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import seohee.weather.domain.DateWeather;
import seohee.weather.domain.Diary;
import seohee.weather.repository.DateWeatherRepository;
import seohee.weather.repository.DiaryRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DiaryService {

    @Value("${openweathermap.key}")
    private String apiKey;

    private final DiaryRepository diaryRepository;
    private final DateWeatherRepository dateWeatherRepository;
    public DiaryService(DiaryRepository diaryRepository,
                        DateWeatherRepository dateWeatherRepository) {
        this.diaryRepository = diaryRepository;
        this.dateWeatherRepository = dateWeatherRepository;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createDiary(LocalDate date, String text) {
        // 날씨 데이터 가져오기 (API에서 가져오기? vs DB에서 가져오기✅)
        DateWeather dateWeather = getDateWeather(date);

        // DB에 저장된 날씨 정보를 불러와서 일기 작성 후 DB 저장
        Diary nowDiary = new Diary();
        nowDiary.setDateWeather(dateWeather);
        nowDiary.setText(text);

        diaryRepository.save(nowDiary);
    }

    @Transactional(readOnly = true)
    public List<Diary> readDiary(LocalDate date) {
        return diaryRepository.findAllByDate(date);
    }

    @Transactional(readOnly = true)
    public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
        return diaryRepository.findAllByDateBetween(startDate, endDate);
    }

    @Transactional
    public void updateDiary(LocalDate date, String text) {
        // 해당 날짜의 첫번째 일기 수정
        Diary nowDiary = diaryRepository.getFirstByDate(date);
        nowDiary.setText(text);

        diaryRepository.save(nowDiary);
    }

    @Transactional
    public void deleteDiary(LocalDate date) {
        diaryRepository.deleteAllByDate(date);
    }

    /** open-weather-map 에서 날씨 데이터 가져오기
     * @return String (날씨 데이터를 문자열 형태로 반환)
     */
    private String getWeatherString() {
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=" + apiKey;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();    // 200, 400 .. 응답코드

            BufferedReader br;
            if(responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));    // 응답객체
            } else {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));    // 오류메시지
            }

            String inputLine;
            StringBuilder response = new StringBuilder();

            while((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            return response.toString();
        } catch (Exception e) {
            return "failed to get response";
        }
    }

    /**
     * 날씨 파싱하기
     * @return 파싱된 결과를 map에 저장하고 반환
     */
    private Map<String, Object> parseWeather(String jsonString) {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;

        try {
            jsonObject = (JSONObject) jsonParser.parse(jsonString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Map<String, Object> resultMap = new HashMap<>();
        JSONObject mainData = (JSONObject) jsonObject.get("main");
        resultMap.put("temp", mainData.get("temp"));

        JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
        JSONObject weatherData = (JSONObject) weatherArray.getFirst();
        resultMap.put("main", weatherData.get("main"));
        resultMap.put("icon", weatherData.get("icon"));

        return resultMap;
    }

    /** 매일 새벽 1시에 날씨 정보를 불러와서 DB에 저장
     */
    @Transactional
    @Scheduled(cron = "0 0 1 * * *")    // cron 을 통해 스케줄링 작업 주기 설정 (초 분 시 일 월 요일)
    public void saveWeatherDate() {
        dateWeatherRepository.save(getWeatherFromApi());
    }

    /**
     * API 를 이용해 날씨 데이터를 가져와서 dateWeather 객체 생성
     * @return DateWeather
     */
    private DateWeather getWeatherFromApi() {
        // openweathermap에서 날씨 데이터 가져오기
        String weatherData = getWeatherString();

        // 받아온 날씨 json 파싱하기
        Map<String, Object> parsedWeather = parseWeather(weatherData);

        DateWeather dateWeather = new DateWeather();
        dateWeather.setDate(LocalDate.now());
        dateWeather.setWeather(parsedWeather.get("main").toString());
        dateWeather.setIcon(parsedWeather.get("icon").toString());
        dateWeather.setTemperature((Double) parsedWeather.get("temp"));
        return dateWeather;
    }

    /** DB에 저장된 날씨 데이터 가져오기 (date_weather 테이블)
     */
    private DateWeather getDateWeather(LocalDate date) {
        List<DateWeather> dateWeatherListFromDB = dateWeatherRepository.findAllByDate(date);
        if(dateWeatherListFromDB.isEmpty()) {
            // 새로 api에서 날씨정보를 가져오기
            // 정책상,, 현재 날씨 가져오기 ✅ vs 날씨없이 일기 쓰기
            return getWeatherFromApi();
        } else {
            return dateWeatherListFromDB.getFirst();
        }
    }

}
