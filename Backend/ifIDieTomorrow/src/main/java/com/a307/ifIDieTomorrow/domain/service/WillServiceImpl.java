package com.a307.ifIDieTomorrow.domain.service;

import com.a307.ifIDieTomorrow.domain.dto.will.GetWillByUserResDto;
import com.a307.ifIDieTomorrow.domain.entity.Will;
import com.a307.ifIDieTomorrow.domain.repository.WillRepository;
import com.a307.ifIDieTomorrow.global.auth.UserPrincipal;
import com.a307.ifIDieTomorrow.global.exception.IllegalArgumentException;
import com.a307.ifIDieTomorrow.global.exception.NoPhotoException;
import com.a307.ifIDieTomorrow.global.exception.NotFoundException;
import com.a307.ifIDieTomorrow.global.util.S3Upload;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class WillServiceImpl implements WillService {
	
	private final S3Upload s3Upload;
	
	private final WillRepository willRepository;
	
	@Override
	public void createWill(Long userId) {
		willRepository.save(Will.builder().userId(userId).build());
	}
	
	@Override
	public GetWillByUserResDto getWillByUserId () throws NotFoundException {
		return willRepository.getByUserId(((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId())
				.orElseThrow(() -> new NotFoundException("유언을 찾을 수 없습니다."));
	}

	@Override
	public Long createSign (MultipartFile photo) throws NoPhotoException, IOException, IllegalArgumentException {
		if (photo == null) throw new NoPhotoException("사진이 없습니다.");
		
		Will will = willRepository.findByUserId(((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId());
		will.createSign(s3Upload.uploadFiles(photo, "will/sign"));
		
		willRepository.save(will);
		
		return will.getWillId();
	}
	
	@Override
	public Long updateContent (String content) {
		Will will = willRepository.findByUserId(((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId());
		will.updateContent(content);
		
		willRepository.save(will);
		
		return will.getWillId();
	}
	
	@Override
	public Long updateVideo (MultipartFile video) throws IOException, IllegalArgumentException, NoPhotoException {
		if (video.isEmpty() || video == null) throw new NoPhotoException("영상이 없습니다.");
		
		Will will = willRepository.findByUserId(((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId());
		String oldUrl = will.getVideoUrl();
		if (oldUrl != null) s3Upload.fileDelete(oldUrl);
		will.updateVideo(s3Upload.uploadFiles(video, "will/video"));
		
		willRepository.save(will);
		
		return will.getWillId();
	}
}
