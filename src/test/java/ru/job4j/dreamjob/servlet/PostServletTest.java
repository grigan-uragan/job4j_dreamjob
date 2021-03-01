package ru.job4j.dreamjob.servlet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.job4j.dreamjob.model.Post;
import ru.job4j.dreamjob.store.MemPostStore;
import ru.job4j.dreamjob.store.PsqlPostStore;
import ru.job4j.dreamjob.store.Store;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PsqlPostStore.class)
public class PostServletTest {

    @Test
    public void whenSavePostThenFindByIdReturnPost() throws ServletException, IOException {
        Store<Post> store = MemPostStore.instOf();
        PowerMockito.mockStatic(PsqlPostStore.class);
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        when(PsqlPostStore.instOf()).thenReturn(store);
        when(req.getParameter("id")).thenReturn("0");
        when(req.getParameter("name")).thenReturn("Job");
        new PostServlet().doPost(req, resp);
        assertThat(new Post(4, "Job"), is(store.findById(4)));
    }
}