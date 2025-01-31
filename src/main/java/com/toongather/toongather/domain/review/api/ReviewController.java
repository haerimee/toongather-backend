package com.toongather.toongather.domain.review.api;


import com.toongather.toongather.domain.member.repository.MemberRepository;
import com.toongather.toongather.domain.review.dto.CreateMyKeywordRequest;
import com.toongather.toongather.domain.review.dto.CreateReviewRecordRequest;
import com.toongather.toongather.domain.review.dto.ReviewDto;
import com.toongather.toongather.domain.review.dto.ReviewKeywordDto;
import com.toongather.toongather.domain.review.dto.ReviewRecordDto;
import com.toongather.toongather.domain.review.dto.UpdateReviewRequest;
import com.toongather.toongather.domain.review.service.ReviewService;
import com.toongather.toongather.domain.webtoon.repository.WebtoonRepository;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/review")
@RequiredArgsConstructor
@RestController
public class ReviewController {

  @Autowired
  private ReviewService reviewService;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private WebtoonRepository webtoonRepository;

  @Value("${file.dir}")
  private String fileDir;

  // 마이리뷰 조회
  @PostMapping("/findAllReview")
  public List<ReviewDto> findAllReview() {
    return reviewService.findAllReview();
  }

  // 나의 키워드 등록
  @PostMapping("/myKeywordRegister")
  public ResponseEntity<ReviewKeywordDto> myKeywordRegister(
      @RequestBody CreateMyKeywordRequest request) {
    ReviewKeywordDto newReviewKeyword = reviewService.registerMyKeyword(request.getKeywordNm(),
        request.getReviewId());
    return new ResponseEntity<>(newReviewKeyword, HttpStatus.OK);
  }

  // 나의 기록 단독 등록
  @PostMapping("/reviewRecordRegister")
  public ResponseEntity<ReviewRecordDto> createReviewRecord(
      @ModelAttribute CreateReviewRecordRequest request) throws IOException {
    ReviewRecordDto reviewRecordDto = reviewService.createReviewRecord(request);
    return new ResponseEntity<ReviewRecordDto>(reviewRecordDto, HttpStatus.OK);
  }

  // 나의 키워드 조회
  @PostMapping("/myKeyword/{reviewId}")
  public ResponseEntity<List<ReviewKeywordDto>> findMyKeyword(@PathVariable String reviewId) {
    List<ReviewKeywordDto> reviewKeywordDtos = reviewService.findMyKeywordByReviewId(reviewId);
    return new ResponseEntity<>(reviewKeywordDtos, HttpStatus.OK);
  }

  // 나의 기록 리스트 조회
  @PostMapping("/reviewRecord/{reviewId}")
  public List<ReviewRecordDto> findMyReviewRecordByReviewId(@PathVariable String reviewId) {
    System.out.println(reviewId + "reviewId");
    return reviewService.findReviewRecordList(reviewId);
  }

  /*
   * 친구에게 추천평 남기기
   * */
  @PostMapping("/registerRecommendComment")
  public ResponseEntity<String> registerRecommendComment(@RequestBody UpdateReviewRequest request) {
    reviewService.updateReview(request);
    return new ResponseEntity<>(HttpStatus.OK);
  }
  // TODO result -> 감싸서 하라고 하는데 이게 reponseEntity에 respone를 넣을까?
/*
  // 리뷰 등록
  @PostMapping("/saveReview")
  public String saveReview(@RequestBody CreateReviewRequest request) {

    log.debug("request.getMemberNo() ={}", request.getId());
    log.debug("request.getWebtoonId() ={}", request.getToonId());
    // 멤버, 웹툰 엔티티조회
    Member member = memberRepository.find(reviewData.getMember().getId());
    Webtoon webtoon = webtoonRepository.findById(reviewData.getWebtoon().getToonId());

    // 리뷰 생성
    Review review = Review.createReview(member, webtoon, reviewData);

    // 리뷰 저장
    reviewService.saveReview(review);
    return review.getReviewId();
  }

 */

  // 리뷰 상세 가져오기
  @GetMapping("/{reviewId}")
  public ResponseEntity<ReviewDto> getReviewDetail(@PathVariable String reviewId) {
    ReviewDto review = reviewService.findOneReview(reviewId);
    return new ResponseEntity<>(review, HttpStatus.OK);
  }


}
