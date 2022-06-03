package com.example.demo.controller;

import com.example.demo.dto.ResponseDTO;
import com.example.demo.dto.BookmarkDTO;
import com.example.demo.model.BookmarkEntity;
import com.example.demo.service.BookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("Bookmark")
public class BookmarkController {

	@Autowired
	private BookmarkService service;

	@GetMapping("/test")
	public ResponseEntity<?> testBookmark() {
		String str = service.testService(); // 테스트 서비스 사용
		List<String> list = new ArrayList<>();
		list.add(str);
		ResponseDTO<String> response = ResponseDTO.<String>builder().data(list).build();
		return ResponseEntity.ok(response);
	}

	@PostMapping
	public ResponseEntity<?> createBookmark(
					@AuthenticationPrincipal String userId,
					@RequestBody BookmarkDTO dto) {
		try {
			// (1) BookmarkEntity로 변환한다.
			BookmarkEntity entity = BookmarkDTO.toEntity(dto);

			// (2) id를 null로 초기화 한다. 생성 당시에는 id가 없어야 하기 때문이다.
			entity.setId(null);

			// (3) 임시 유저 아이디를 설정 해 준다. 이 부분은 4장 인증과 인가에서 수정 할 예정이다. 지금은 인증과 인가 기능이 없으므로 한 유저(temporary-user)만 로그인 없이 사용 가능한 어플리케이션인 셈이다
			entity.setUserId(userId);

			// (4) 서비스를 이용해 Bookmark엔티티를 생성한다.
			List<BookmarkEntity> entities = service.create(entity);

			// (5) 자바 스트림을 이용해 리턴된 엔티티 리스트를 BookmarkDTO리스트로 변환한다.

			List<BookmarkDTO> dtos = entities.stream().map(BookmarkDTO::new).collect(Collectors.toList());

			// (6) 변환된 BookmarkDTO리스트를 이용해ResponseDTO를 초기화한다.
			ResponseDTO<BookmarkDTO> response = ResponseDTO.<BookmarkDTO>builder().data(dtos).build();

			// (7) ResponseDTO를 리턴한다.
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			// (8) 혹시 예외가 나는 경우 dto대신 error에 메시지를 넣어 리턴한다.

			String error = e.getMessage();
			ResponseDTO<BookmarkDTO> response = ResponseDTO.<BookmarkDTO>builder().error(error).build();
			return ResponseEntity.badRequest().body(response);
		}
	}

	@GetMapping
	public ResponseEntity<?> retrieveBookmarkList(
					@AuthenticationPrincipal String userId) {
		System.out.println("UserID : " + userId);
		// (1) 서비스 메서드의 retrieve메서드를 사용해 Bookmark리스트를 가져온다
		List<BookmarkEntity> entities = service.retrieve(userId);

		// (2) 자바 스트림을 이용해 리턴된 엔티티 리스트를 BookmarkDTO리스트로 변환한다.
		List<BookmarkDTO> dtos = entities.stream().map(BookmarkDTO::new).collect(Collectors.toList());

		// (6) 변환된 BookmarkDTO리스트를 이용해ResponseDTO를 초기화한다.
		ResponseDTO<BookmarkDTO> response = ResponseDTO.<BookmarkDTO>builder().data(dtos).build();

		// (7) ResponseDTO를 리턴한다.
		return ResponseEntity.ok(response);
	}


	@PutMapping
	public ResponseEntity<?> updateBookmark(@AuthenticationPrincipal String userId,
	                                    @RequestBody BookmarkDTO dto) {
		// (1) dto를 entity로 변환한다.
		BookmarkEntity entity = BookmarkDTO.toEntity(dto);

		// (2) id를 userId 초기화 한다.
		entity.setUserId(userId);

		// (3) 서비스를 이용해 entity를 업데이트 한다.
		List<BookmarkEntity> entities = service.update(entity);

		// (4) 자바 스트림을 이용해 리턴된 엔티티 리스트를 BookmarkDTO리스트로 변환한다.
		List<BookmarkDTO> dtos = entities.stream().map(BookmarkDTO::new).collect(Collectors.toList());

		// (5) 변환된 BookmarkDTO리스트를 이용해ResponseDTO를 초기화한다.
		ResponseDTO<BookmarkDTO> response = ResponseDTO.<BookmarkDTO>builder().data(dtos).build();

		// (6) ResponseDTO를 리턴한다.
		return ResponseEntity.ok(response);
	}

	@DeleteMapping
	public ResponseEntity<?> deleteBookmark(
					@AuthenticationPrincipal String userId,
					@RequestBody BookmarkDTO dto) {
		try {
			// (1) BookmarkEntity로 변환한다.
			BookmarkEntity entity = BookmarkDTO.toEntity(dto);

			// (2) 임시 유저 아이디를 설정 해 준다.
			entity.setUserId(userId);

			// (3) 서비스를 이용해 entity를 삭제 한다.
			List<BookmarkEntity> entities = service.delete(entity);

			// (4) 자바 스트림을 이용해 리턴된 엔티티 리스트를 BookmarkDTO리스트로 변환한다.
			List<BookmarkDTO> dtos = entities.stream().map(BookmarkDTO::new).collect(Collectors.toList());

			// (5) 변환된 BookmarkDTO리스트를 이용해ResponseDTO를 초기화한다.
			ResponseDTO<BookmarkDTO> response = ResponseDTO.<BookmarkDTO>builder().data(dtos).build();

			// (6) ResponseDTO를 리턴한다.
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			// (8) 혹시 예외가 나는 경우 dto대신 error에 메시지를 넣어 리턴한다.
			String error = e.getMessage();
			ResponseDTO<BookmarkDTO> response = ResponseDTO.<BookmarkDTO>builder().error(error).build();
			return ResponseEntity.badRequest().body(response);
		}
	}

}
