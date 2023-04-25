package com.a307.ifIDieTomorrow.domain.service;

import com.a307.ifIDieTomorrow.domain.dto.category.CreateCategoryDto;
import com.a307.ifIDieTomorrow.domain.dto.category.CreateCategoryResDto;
import com.a307.ifIDieTomorrow.domain.dto.category.UpdateCategoryDto;
import com.a307.ifIDieTomorrow.domain.dto.community.GetBucketWithCommentDto;
import com.a307.ifIDieTomorrow.domain.dto.photo.*;
import com.a307.ifIDieTomorrow.domain.entity.Category;
import com.a307.ifIDieTomorrow.domain.entity.Photo;
import com.a307.ifIDieTomorrow.domain.repository.CategoryRepository;
import com.a307.ifIDieTomorrow.domain.repository.PhotoRepository;
import com.a307.ifIDieTomorrow.global.auth.UserPrincipal;
import com.a307.ifIDieTomorrow.global.exception.IllegalArgumentException;
import com.a307.ifIDieTomorrow.global.exception.NoPhotoException;
import com.a307.ifIDieTomorrow.global.exception.NotFoundException;
import com.a307.ifIDieTomorrow.global.exception.UnAuthorizedException;
import com.a307.ifIDieTomorrow.global.util.S3Upload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {
	
	private final String PHOTO = "photo";
	
	private final S3Upload s3Upload;
	
	private final CategoryRepository categoryRepository;
	
	private final PhotoRepository photoRepository;
	
	///////////////////////
	// APIs For CATEGORY //
	///////////////////////
	
	@Override
	public CreateCategoryResDto createCategory (CreateCategoryDto data) {
		Category category = Category.builder().
				userId(((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId()).
				name(data.getName()).
				build();
		
		return CreateCategoryResDto.toDto(categoryRepository.save(category));
	}
	
	@Override
	public List<CreateCategoryResDto> getCategory () {
		return categoryRepository.findAllByUserId(((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId());
	}
	
	@Override
	public CreateCategoryResDto updateCategory (UpdateCategoryDto data) throws NotFoundException {
		Category category = categoryRepository.findByCategoryId(data.getCategoryId())
				.orElseThrow(() -> new NotFoundException("존재하지 않는 카테고리 ID 입니다."));
		
		category.updateCategory(data.getName());
		
		return CreateCategoryResDto.toDto(categoryRepository.save(category));
	}
	
	@Override
	public Long deleteCategory (Long categoryId) throws NotFoundException {
		Category category = categoryRepository.findByCategoryId(categoryId)
				.orElseThrow(() -> new NotFoundException("존재하지 않는 카테고리 ID 입니다."));
		
		// 카테고리에 엮인 사진 모두 삭제
		photoRepository.deleteAllInBatchByPhotoIdIn(photoRepository.findAllPhotoIdByCategory_CategoryId(categoryId));
		
		// 카테고리 삭제
		categoryRepository.delete(category);
		
		return categoryId;
	}
	
	//////////////////////////
	// APIs For PHOTO CLOUD //
	//////////////////////////
	
	@Override
	public CreatePhotoResDto createPhoto (CreatePhotoDto data, MultipartFile photo) throws IllegalArgumentException, IOException, NoPhotoException, NotFoundException, UnAuthorizedException {
		if (photo == null) throw new NoPhotoException("사진이 없습니다.");
		
		Category category = categoryRepository.findByCategoryId(data.getCategoryId())
				.orElseThrow(() -> new NotFoundException("존재하지 않는 카테고리 ID 입니다."));
		
		Long userId = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
		
		// 공용 카테고리가 아니면서 다른 유저가 만든 카테고리에 업로드하려 할 때
		if (category.getUserId() != 0 && !category.getUserId().equals(userId)) throw new UnAuthorizedException("접근할 수 없는 카테고리 ID 입니다.");
		
		Photo photoEntity = Photo.builder().
				category(category).
				userId(userId).
				imageUrl(s3Upload.uploadFiles(photo, PHOTO)).
				caption(data.getCaption()).
				build();
		
		return CreatePhotoResDto.toDto(photoRepository.save(photoEntity));
	}
	
	@Override
	public CreatePhotoResDto updatePhoto (UpdatePhotoDto data) throws NotFoundException, UnAuthorizedException {
		Photo photo = photoRepository.findByPhotoId(data.getPhotoId())
				.orElseThrow(() -> new NotFoundException("존재하지 않는 포토 ID 입니다."));
		
		Long userId = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
		
		// 본인의 사진이 아닌 경우
		if (!photo.getUserId().equals(userId)) throw new UnAuthorizedException("접근할 수 없는 포토 ID 입니다.");
		
		photo.updateCategory(data.getCaption());
		
		return CreatePhotoResDto.toDto(photoRepository.save(photo));
	}
	
	@Override
	public Long deletePhoto (Long photoId) throws NotFoundException, UnAuthorizedException {
		Photo photo = photoRepository.findByPhotoId(photoId)
				.orElseThrow(() -> new NotFoundException("존재하지 않는 포토 ID 입니다."));
		
		Long userId = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
		
		// 본인의 사진이 아닌 경우
		if (!photo.getUserId().equals(userId)) throw new UnAuthorizedException("삭제할 수 없는 포토 ID 입니다.");
		
		// S3에서 사진 삭제
		s3Upload.fileDelete(photo.getImageUrl());
		
		// Database 에서 사진 삭제
		photoRepository.delete(photo);
		
		return photoId;
	}
	
	@Override
	public GetPhotoByCategoryResDto getPhotoByCategory (Long categoryId) throws NotFoundException, UnAuthorizedException {
		Category category = categoryRepository.findByCategoryId(categoryId).
				orElseThrow(() -> new NotFoundException("존재하지 않는 카테고리 ID 입니다."));
		 
		// 공용 카테고리가 아니면서 다른 유저가 만든 카테고리에 접근하려 할 때
		if (category.getUserId() != 0 && !category.getUserId().equals(((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId()))
			throw new UnAuthorizedException("접근할 수 없는 카테고리 ID 입니다.");
		
		return new GetPhotoByCategoryResDto(
				CreateCategoryResDto.toDto(category),
				photoRepository.findAllPhotoByCategory_CategoryId(categoryId)
		);
	}
	
	@Override
	public List<GetPhotoByCategoryResDto> getPhotoByUser () {
		Long userId = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
		List<CreateCategoryResDto> categories = categoryRepository.findAllByUserId(userId);
		List<GetPhotoByCategoryResDto> list = new ArrayList<>();
		categories.forEach(x -> list.add(
				new GetPhotoByCategoryResDto(x, photoRepository.findAllPhotoByCategory_CategoryId(x.getCategoryId())))
		);
		
		return list;
	}
	
}