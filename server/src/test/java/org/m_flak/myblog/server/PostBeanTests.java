package org.m_flak.myblog.server;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.m_flak.myblog.server.db.ServerDatabase;

public class PostBeanTests extends TestUsingDB {
    private PostBeanTestsFixture pbtFixture;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        pbtFixture = new PostBeanTestsFixture(
            ServerDatabase.inst().driv(),
            ServerDatabase.inst().conn(),
            ServerDatabase.inst().db().get()
        );
    }

    @Test
    public void createPostFromBeanTest() throws Exception {
        pbtFixture.createMockUsers();
        pbtFixture.createMockPost();

        assertFalse(pbtFixture.mockPostBean.getPostID() == pbtFixture.mockPostBeanInDB.getPostID());

        assertTrue(pbtFixture.mockPostBean.getPosterID() == pbtFixture.mockPostBeanInDB.getPosterID());
        assertTrue(pbtFixture.mockPostBean.getTitle().equals(pbtFixture.mockPostBeanInDB.getTitle()));
        assertTrue(pbtFixture.mockPostBean.getContents().equals(pbtFixture.mockPostBeanInDB.getContents()));
    }

}
