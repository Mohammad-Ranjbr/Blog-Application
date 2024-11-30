package com.blog_application.repository.notice;

import com.blog_application.model.notice.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    @Query(value = "from Notice n where CURRENT_DATE  between n.noticeStartDate and n.noticeEndDate")
    List<Notice> findAllActiveNotices();

}
