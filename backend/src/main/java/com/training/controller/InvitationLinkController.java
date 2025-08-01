package com.training.controller;

import com.training.dto.ApiResponse;
import com.training.dto.InvitationLinkDto;
import com.training.dto.JoinRequestDto;
import com.training.entity.InvitationLink;
import com.training.service.InvitationLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invitations")
@CrossOrigin(origins = "*")
public class InvitationLinkController {

    @Autowired
    private InvitationLinkService invitationLinkService;

    // 分页获取邀请链接列表
    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<InvitationLink>>> getInvitationLinksWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<InvitationLink> links = invitationLinkService.findInvitationLinksWithPagination(pageable, keyword);
            return ResponseEntity.ok(ApiResponse.success("获取邀请链接列表成功", links));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取邀请链接列表失败: " + e.getMessage()));
        }
    }

    // Admin: Get all links
    @GetMapping
    public ResponseEntity<ApiResponse<List<InvitationLink>>> getAllLinks() {
        List<InvitationLink> links = invitationLinkService.getAllInvitationLinks();
        return ResponseEntity.ok(ApiResponse.success("成功获取邀请链接列表", links));
    }

    // Admin: Create a new link
    @PostMapping
    public ResponseEntity<ApiResponse<InvitationLink>> createLink(@RequestBody InvitationLinkDto invitationLinkDto) {
        try {
            InvitationLink createdLink = invitationLinkService.createInvitationLink(invitationLinkDto);
            return ResponseEntity.ok(ApiResponse.success("成功创建邀请链接", createdLink));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("创建失败: " + e.getMessage()));
        }
    }

    // Admin: Delete a link
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLink(@PathVariable Long id) {
        try {
            invitationLinkService.deleteInvitationLink(id);
            return ResponseEntity.ok(ApiResponse.success("成功删除邀请链接"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("删除失败: " + e.getMessage()));
        }
    }

    // User: Join with a link
    @PostMapping("/join")
    public ResponseEntity<ApiResponse<Void>> joinWithLink(
            @RequestBody JoinRequestDto joinRequestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(401).body(ApiResponse.error("用户未登录或会话已过期"));
            }
            invitationLinkService.joinCoursesByInvitation(
                joinRequestDto.getLinkCode(),
                joinRequestDto.getPassword(),
                userDetails.getUsername()
            );
            return ResponseEntity.ok(ApiResponse.success("成功加入课程"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
} 