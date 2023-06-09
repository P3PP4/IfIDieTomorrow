package com.a307.ifIDieTomorrow.domain.service;

import com.a307.ifIDieTomorrow.domain.dto.bucket.CreateBucketResDto;
import com.a307.ifIDieTomorrow.domain.dto.bucket.CreateBucketWithTitleDto;
import com.a307.ifIDieTomorrow.domain.dto.bucket.UpdateBucketDto;
import com.a307.ifIDieTomorrow.domain.entity.Bucket;
import com.a307.ifIDieTomorrow.domain.entity.User;
import com.a307.ifIDieTomorrow.domain.repository.BucketRepository;
import com.a307.ifIDieTomorrow.domain.repository.CommentRepository;
import com.a307.ifIDieTomorrow.global.auth.ProviderType;
import com.a307.ifIDieTomorrow.global.auth.UserPrincipal;
import com.a307.ifIDieTomorrow.global.exception.IllegalArgumentException;
import com.a307.ifIDieTomorrow.global.exception.NotFoundException;
import com.a307.ifIDieTomorrow.global.exception.UnAuthorizedException;
import com.a307.ifIDieTomorrow.global.util.S3Upload;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.MetadataException;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class BucketServiceImplTest {

	@InjectMocks
	private BucketServiceImpl bucketService;
	
	@Mock
	private BucketRepository bucketRepository;
	
	@Mock
	private CommentRepository commentRepository;
	
	@Mock
	private S3Upload s3Upload;
	
	private User user;

	/**
	 * 테스트용 유저 및 인증 객체 생성
	 */
	@BeforeEach
	void setUp() {
		user = User.builder()
				.userId(1L)
				.name("tom")
				.nickname("tommy")
				.email("tom@email.com")
				.sendAgree(false)
				.newCheck(true)
				.deleted(false)
				.providerType(ProviderType.NAVER)
				.build();

		UserPrincipal userPrincipal = UserPrincipal.create(user);
		TestingAuthenticationToken authentication = new TestingAuthenticationToken(userPrincipal, null);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	@AfterEach
	void tearDown() {
		bucketRepository.deleteAllInBatch();
		commentRepository.deleteAllInBatch();
	}

	@DisplayName("버킷 생성")
	@Nested
	class CreateBucketTest {

		@DisplayName("성공 케이스")
		@Nested
		class NormalScenario {

			@Test
			@DisplayName("버킷 제목 생성")
			void createBucketWithTitle() {

				// given
				CreateBucketWithTitleDto data = new CreateBucketWithTitleDto("Test Title");

				Bucket savedBucket = Bucket.builder()
						.bucketId(1L)
						.userId(1L)
						.title(data.getTitle())
						.build();

				/**
				 * 정상 동작 stubbing
				 */
				given(bucketRepository.save(any(Bucket.class))).willReturn(savedBucket);

				// when
				CreateBucketResDto result = bucketService.createBucketWithTitle(data);
				ArgumentCaptor<Bucket> bucketCaptor = ArgumentCaptor.forClass(Bucket.class);

				// then
				/**
				 * 동작 검증
				 * 버킷이 저장 되는가
				 */
				then(bucketRepository).should().save(bucketCaptor.capture());

				/**
				 * 전달된 인자 검증
				 * 유저 아이디가 정상적으로 들어갔는가
				 */
				Bucket capturedBucket = bucketCaptor.getValue();
				BDDAssertions.then(capturedBucket.getUserId()).isEqualTo(1L);

				/**
				 * 결괏값 검증
				 * 버킷 아이디
				 * 유저아이디
				 */
				BDDAssertions.then(result.getBucketId()).isEqualTo(savedBucket.getBucketId());
				BDDAssertions.then(result.getUserId()).isEqualTo(savedBucket.getUserId());
				
			}
			
		}

	}
	
	@Nested
	@DisplayName("버킷 업데이트")
	class UpdateBucketTest {
		
		@Nested
		@DisplayName("성공 케이스")
		class NormalScenario {
			
			@Test
			@DisplayName("사진 없는 내용 수정")
			void updateAndReplacePhoto(){
			
			}
			
			@Test
			@DisplayName("사진 포함한 내용 수정")
			void updateWithNewPhoto() throws IOException, IllegalArgumentException, NotFoundException, UnAuthorizedException, ImageProcessingException, MetadataException {
				
				// given
				
				/**
				 * 기존 버킷
				 */
				Bucket existingBucket = Bucket.builder()
						.bucketId(1L)
						.title("Test Title")
						.userId(1L)
						.content(null)
						.secret(true)
						.report(0)
						.complete(null)
						.imageUrl(null)
						.build();
				System.out.println(existingBucket.getBucketId());
				
				/**
				 * 수정 내역
				 */
				UpdateBucketDto data = UpdateBucketDto.builder()
						.bucketId(1L)
						.title("updated title")
						.content("updated content")
						.secret(false)
						.complete("2023-05-09")
						.updatePhoto(true)
						.build();
				
				MultipartFile photo = new MockMultipartFile("file", "new_test.jpg", "image/jpeg", "new_test".getBytes());
				
				/**
				 * 수정된 버킷 (expected)
				 */
				Bucket updatedBucket = Bucket.builder()
						.bucketId(1L)
						.title(data.getTitle())
						.userId(1L)
						.complete(data.getComplete())
						.content(data.getContent())
						.secret(data.getSecret())
						.report(0)
						.imageUrl("https://example.com/new_test.jpg")
						.build();
				
				/**
				 * 스터빙
				 */
				given(bucketRepository.findByBucketId(data.getBucketId())).willReturn(Optional.of(existingBucket));
				given(s3Upload.upload(photo, "bucket")).willReturn("https://example.com/new_test.jpg");
				given(bucketRepository.save(any(Bucket.class))).willReturn(updatedBucket);
				
				System.out.println(data.getBucketId());
				// when
				CreateBucketResDto result = bucketService.updateBucket(data, photo);
				
				// then
				
				/**
				 * 동작 검증
				 * 버킷 조회
				 * 신규 사진 업로드
				 */
				then(bucketRepository).should().findByBucketId(data.getBucketId());
				then(s3Upload).should(never()).delete(any(String.class));
				then(s3Upload).should().upload(photo, "bucket");
				
				/**
				 * 결과 검증
				 */
				BDDAssertions.then(result.getTitle()).isEqualTo(updatedBucket.getTitle());
				BDDAssertions.then(result.getContent()).isEqualTo(updatedBucket.getContent());
				BDDAssertions.then(result.getSecret()).isEqualTo(updatedBucket.getSecret());
				BDDAssertions.then(result.getImageUrl()).isEqualTo(updatedBucket.getImageUrl());
				
			}
			
			@Test
			@DisplayName("사진 없이 내용 수정만")
			void updatedWithNoPhoto(){
			
			}
			
			@Test
			@DisplayName("기존 사진 삭제 + 내용 수정")
			void updatedAndDeletePhoto(){
			
			}
			
		}
		
		@Nested
		@DisplayName("예외 케이스")
		class ExceptionScenario {
			
			@Test
			@DisplayName("다이어리 아이디 잘못된 경우")
			void wrongDiaryId() {
			
			}
			
			@Test
			@DisplayName("작성자랑 요청 사용자가 다른 경우")
			void notTheAuthor() {
			
			}
			
		}
		
	}

}