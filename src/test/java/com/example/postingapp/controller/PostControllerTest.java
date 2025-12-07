package com.example.postingapp.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.postingapp.entity.Post;
import com.example.postingapp.service.PostService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PostControllerTest {
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private PostService postService;
	
	@Test
	@WithUserDetails("taro.samurai@example.com")
	public void ログイン済みの場合は投稿一覧ページが正しく表示される() throws Exception{
		mockMvc.perform(get("/posts"))
		       .andExpect(status().isOk())
		       .andExpect(view().name("posts/index"));
	}
	
	@Test
	public void 未ログインの場合は投稿一覧ページからログインページにリダイレクトする() throws Exception{
		mockMvc.perform(get("/posts"))
		       .andExpect(status().is3xxRedirection())
		       .andExpect(redirectedUrl("http://localhost/login"));
	}
	
	@Test
	@WithUserDetails("taro.samurai@example.com")
	public void ログイン済みの場合は投稿詳細ページが正しく表示される() throws Exception{
		mockMvc.perform(get("/posts/1"))
		       .andExpect(status().isOk())
		       .andExpect(view().name("posts/show"));
	}
	
	@Test
	public void 未ログインの場合は投稿詳細ページからログインページにリダイレクトする() throws Exception{
		mockMvc.perform(get("/post/1"))
		       .andExpect(status().is3xxRedirection())
		       .andExpect(redirectedUrl("http://localhost/login"));
	}
	
	@Test
	@WithUserDetails("taro.samurai@example.com")
	public void ログイン済みの場合は投稿作成ページが正しく表示される() throws Exception{
		mockMvc.perform(get("/posts/register"))
		       .andExpect(status().isOk())
		       .andExpect(view().name("posts/register"));
	}
	
	@Test
	public void 未ログインの場合は投稿作成ページからログインページにリダイレクトする() throws Exception{
		mockMvc.perform(get("/posts/register"))
		       .andExpect(status().is3xxRedirection())
		       .andExpect(redirectedUrl("http://localhost/login"));
	}
	
	@Test
	@WithUserDetails("taro.samurai@example.com")
	@Transactional
	public void ログイン済みの場合は投稿作成後に一覧ページにリダイレクトする() throws Exception{
		mockMvc.perform(post("/posts/create").with(csrf()).param("title", "テストタイトル").param("content","テスト内容"))
		       .andExpect(status().is3xxRedirection())
		       .andExpect(redirectedUrl("/posts"));
		
		Post post = postService.findFirstPostByOrderByIdDesc();
		assertThat(post.getTitle()).isEqualTo("テストタイトル");
		assertThat(post.getContent()).isEqualTo("テスト内容");
	}
	
	@Test
	@Transactional
	public void 未ログインの場合は投稿をせずにログインページにリダイレクトする() throws Exception {
		mockMvc.perform(post("/posts/create").with(csrf()).param("title","テストタイトル").param("content","テスト内容"))
		       .andExpect(status().is3xxRedirection())
		       .andExpect(redirectedUrl("http://localhost/login"));
		
		Post post = postService.findFirstPostByOrderByIdDesc();
		assertThat(post.getTitle()).isNotEqualTo("テストタイトル");
		assertThat(post.getContent()).isNotEqualTo("テスト内容");
	}
	
	@Test
	@WithUserDetails("taro.samurai@example.com")
	public void ログイン済みの場合は自身の投稿編集ページが正しく表示される() throws Exception{
		mockMvc.perform(get("/posts/1/edit"))
		       .andExpect(status().isOk())
		       .andExpect(view().name("posts/edit"));
	}
	
	@Test
	@WithUserDetails("jiro.samurai@example.com")
	public void ログイン済みの場合は他人の投稿編集ページから投稿一覧ページにリダイレクトする() throws Exception{
		mockMvc.perform(get("/posts/1/edit"))
		       .andExpect(status().is3xxRedirection())
		       .andExpect(redirectedUrl("/posts"));
	}
	
	@Test
	public void 未ログインの場合は投稿編集ページからログインページにリダイレクトする() throws Exception{
		mockMvc.perform(get("/posts/1/edit"))
		       .andExpect(status().is3xxRedirection())
		       .andExpect(redirectedUrl("http://localhost/login"));
	}
}
