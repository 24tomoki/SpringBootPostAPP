package com.example.postingapp.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PostControllerTest {
	@Autowired
	private MockMvc mockMvc;
	
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
}
