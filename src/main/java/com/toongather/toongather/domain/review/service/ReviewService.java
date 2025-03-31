package com.toongather.toongather.domain.review.service;


import com.toongather.toongather.domain.keyword.domain.Keyword;
import com.toongather.toongather.domain.keyword.service.KeywordService;
import com.toongather.toongather.domain.member.domain.Member;
import com.toongather.toongather.domain.member.service.MemberService;
import com.toongather.toongather.domain.review.domain.Review;
import com.toongather.toongather.domain.review.domain.ReviewSortType;
import com.toongather.toongather.domain.review.dto.CreateReviewRequest;
import com.toongather.toongather.domain.review.dto.ReviewDto;
import com.toongather.toongather.domain.review.dto.ReviewShareRequest;
import com.toongather.toongather.domain.review.dto.ReviewShareResponse;
import com.toongather.toongather.domain.review.dto.UpdateReviewRequest;
import com.toongather.toongather.domain.review.repository.ReviewRepository;
import com.toongather.toongather.domain.webtoon.domain.Webtoon;
import com.toongather.toongather.domain.webtoon.repository.WebtoonRepository;
import com.toongather.toongather.global.common.util.file.FileStore;


import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;


import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {

  @Autowired
  private ReviewRepository reviewRepository;

  @Autowired
  private WebtoonRepository webtoonRepository;

  @Autowired
  private KeywordService keywordService;

  @Autowired
  private MemberService memberService;

  @Autowired
  private ReviewKeywordService reviewKeywordService;

  //@Autowired
  //private FileService fileService;

  private final FileStore fileStore;
  private final int REVIEW_PAGE_SIZE = 10;

  public Page<ReviewDto> findAllWithSortType(ReviewSortType reviewSortType, Pageable pageable) {

    Sort sort;
    if (reviewSortType == null) {
      sort = Sort.by(Direction.DESC, "regDt");
    } else {
      switch (reviewSortType) {
        case STAR_DESC:
          sort = Sort.by(Direction.DESC, "star");
          break;
        case STAR_ASC:
          sort = Sort.by(Direction.ASC, "star");
          break;
        case CREATE_DATE_DESC:
          sort = Sort.by(Direction.DESC, "regDt");
          break;
        default:
          sort = Sort.by(Direction.DESC, "regDt");
          break;
      }
    }
    Pageable pageRequest = PageRequest.of(pageable.getPageNumber(), REVIEW_PAGE_SIZE,
        sort);   // TODO 페이지 추후변경

    return reviewRepository.findAll(pageRequest)
        .map(review -> ReviewDto.builder()
            .title(review.getWebtoon().getTitle())
            .recommendComment(review.getRecommendComment())
            .imgPath(review.getWebtoon().getImgPath())
            .star(review.getStar())
            .reviewDate(review.getRegDt())
            .build());
  }

  public List<ReviewShareResponse> shareReviewWithFriend(ReviewShareRequest request) {
    List<Review> reviews = reviewRepository.findAllById(request.getReviewIds());
    return reviews.stream().map(review -> ReviewShareResponse.from(review))
        .collect(Collectors.toList());
  }

  @Transactional
  public Long saveReview(Review review) {
    if (review.getReviewId() == null) {
      reviewRepository.save(review);
    }
    return review.getReviewId();
  }

  @Transactional
  public Long createReview(CreateReviewRequest request) {
    // 멤버, 웹툰 엔티티조회
    Member member = memberService.findMemberEntityById(request.getMemberId());
    Webtoon webtoon = webtoonRepository.findById(request.getToonId()).get();

    // 리뷰 엔티티 생성
    Review review = request.toEntity(member, webtoon);
    Long reviewId = saveReview(review);

//    // 키워드 여부 체크
//    if (!request.getKeywords().isEmpty()) {
//      for (String keywordNm : request.getKeywords()) {
//        Keyword keyword = keywordService.createKeyword(keywordNm);
//        reviewKeywordService.createReviewKeyword(review, keyword);
//      }
//    }

    return reviewId;
  }

  @Transactional
  public Review createDefaultReview(Long memberId, Long toonId) {
    Member member = memberService.findMemberEntityById(memberId);
    Webtoon webtoon = webtoonRepository.findById(toonId).get();
    Review review = Review.builder()
        .toon(webtoon)
        .member(member).build();
    reviewRepository.save(review);
    return review;
  }

  public Review findById(Long reviewId) {
    return reviewRepository.findById(reviewId)
        .orElseThrow(() -> new NoSuchElementException("리뷰를 찾을 수 없습니다: " + reviewId));
  }

  @Transactional
  public void updateReview(UpdateReviewRequest request) {
    Review review = reviewRepository.findById(request.getReviewId())
        .orElseThrow(() -> new NoSuchElementException("리뷰를 찾을 수 없습니다: " + request.getReviewId()));
    review.updateReview(request.getRecommendComment(), request.getStar());

//    if (request.getKeywords().isEmpty()) {
//      reviewKeywordService.deleteByReviewId(request.getReviewId());
//    } else {
//      reviewKeywordService.deleteByReviewId(request.getReviewId());
//      for (String keywordNm : request.getKeywords()) {
//        Keyword keyword = keywordService.createKeyword(keywordNm);
//        reviewKeywordService.createReviewKeyword(review, keyword);
//      }
//    }
  }

  @Transactional
  public void deleteReview(Long reviewId) {
    if (!reviewRepository.existsById(reviewId)) {
      throw new NoSuchElementException("삭제 할 리뷰가 없습니다.");
    }
    //reviewKeywordService.deleteByReviewId(reviewId);
    reviewRepository.deleteById(reviewId);
  }


}
