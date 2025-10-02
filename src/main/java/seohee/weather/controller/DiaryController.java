package seohee.weather.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import seohee.weather.domain.Diary;
import seohee.weather.service.DiaryService;

import java.time.LocalDate;
import java.util.List;

@RestController
public class DiaryController {

    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    /** 다이어리 작성
      */
    @Operation(summary = "일기 텍스트와 날씨를 이용해서 DB에 저장", description = "일기를 생성합니다.")
    @PostMapping("/create/diary")
    void createDiary(@RequestParam
                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                     @RequestBody String text) {
        diaryService.createDiary(date, text);
    }

    /** 일기 조회
     */
    @Operation(summary = "선택한 날짜에 모든 일기 데이터를 가져옵니다.")
    @GetMapping("read/diary")
    List<Diary> readDiary(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "날짜 형식 : yyyy-MM-dd",example = "2025-02-02")
            LocalDate date
            ) {
        return diaryService.readDiary(date);
    }

    @Operation(summary = "선택한 기간 중 모든 일기 데이터를 가져옵니다.")
    @GetMapping("read/diaries")
    List<Diary> readDiaries(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "조회할 기간의 시작 날짜", example = "2025-02-02")
            LocalDate startDate,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "조회할 기간의 마지막 날짜", example = "2025-02-02")
            LocalDate endDate
            ) {
        return diaryService.readDiaries(startDate, endDate);
    }

    /** 일기 수정
     */
    @PutMapping("update/diary")
    void updateDiary(@RequestParam
                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                     @RequestBody String text
                     ) {
        diaryService.updateDiary(date, text);
    }

    /** 일기 삭제
     */
    @DeleteMapping("/delete/diary")
    void deleteDiary(@RequestParam
                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
                     ) {
        diaryService.deleteDiary(date);
    }
}
