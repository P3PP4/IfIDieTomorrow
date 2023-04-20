package com.a307.ifIDieTomorrow.domain.repository;

import com.a307.ifIDieTomorrow.domain.dto.diary.GetDiaryByUserResDto;
import com.a307.ifIDieTomorrow.domain.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

	@Query("SELECT new com.a307.ifIDieTomorrow.domain.dto.diary.GetDiaryByUserResDto" +
			"(d.diaryId, d.userId, d.title, d.content, d.imageUrl, d.secret, d.report, d.createdAt, d.updatedAt, COUNT(c.commentId)) " +
			"FROM Diary d " +
			"LEFT JOIN Comment c " +
			"ON d.diaryId = c.typeId " +
			"WHERE c.type = true " +
			"AND d.userId = :userId " +
			"GROUP BY d.diaryId")
	List<GetDiaryByUserResDto> findAllByUserIdWithCommentCount (@Param("userId") Long userId);




}
