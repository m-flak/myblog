package org.m_flak.myblog.server.db.methods;

import java.math.BigInteger;
import java.sql.Connection;

import org.apache.empire.db.DBCommand;
import org.apache.empire.db.DBReader;
import org.apache.empire.db.DBRecord;
import org.apache.empire.exceptions.EmpireException;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.m_flak.myblog.server.data.SummaryPostBean;
import org.m_flak.myblog.server.db.DatabaseDate;
import org.m_flak.myblog.server.db.model.Database;
import org.m_flak.myblog.server.data.PostBean;

public class PostMethods {
    protected PostMethods() {
    }

    public static long countPosts(Database db, Connection con) {
        DBCommand countCmd = db.createCommand();
        countCmd.select(db.T_POSTS.count());
        return db.querySingleLong(countCmd, 0L, con);
    }

    public static long createPostFromBean(PostBean bean, Database db, Connection con) {
        DBRecord newPost = new DBRecord();
        newPost.create(db.T_POSTS);
        newPost.setRecordValues(bean);
        newPost.update(con);

        BigInteger itsID = (BigInteger) newPost.getValue(db.T_POSTS.C_POST_ID);
        return itsID.longValue();
    }

    public static PostBean getPostById(long postId, Database db, Connection con) {
        DBCommand cmdSelect = db.createCommand();
        cmdSelect.select(db.T_POSTS.C_POST_ID,
                db.T_POSTS.C_POSTER_ID,
                db.T_POSTS.C_TITLE,
                db.T_POSTS.C_DATE_POSTED,
                db.T_POSTS.C_CONTENTS
        );
        cmdSelect.where(db.T_POSTS.C_POST_ID.is(postId));

        PostBean gottenPost;
        DBReader reader = new DBReader();
        try {
            reader.open(cmdSelect, con);
            gottenPost = (PostBean) reader.getBeanList(PostBean.class).toArray()[0];
        }
        catch (EmpireException ee) {
            gottenPost = new PostBean(-1, -1, "Invalid Post", new DatabaseDate().toString(), "Invalid Post");
        }
        finally {
            reader.close();
        }

        return gottenPost;
    }

    public static MutableList<PostBean> getAllPosts(Database db, Connection con) {
        final long totalPosts = countPosts(db, con);
        final MutableList<PostBean> allThePosts = FastList.newList((int)totalPosts);

        DBCommand cmdSelect = db.createCommand();
        cmdSelect.select(db.T_POSTS.C_POST_ID,
                db.T_POSTS.C_POSTER_ID,
                db.T_POSTS.C_TITLE,
                db.T_POSTS.C_DATE_POSTED,
                db.T_POSTS.C_CONTENTS
        );
        cmdSelect.orderBy(db.T_POSTS.C_DATE_POSTED, true);

        DBReader reader = new DBReader();
        try {
            reader.open(cmdSelect, con);
            allThePosts.addAll(0, reader.getBeanList(PostBean.class));
        }
        catch (Exception e) {
            allThePosts.clear();
        }
        finally {
            reader.close();
        }

        return allThePosts;
    }

    public static MutableList<SummaryPostBean> getAllSummaryPosts(Database db, Connection con) {
        final long totalPosts = countPosts(db, con);
        final MutableList<SummaryPostBean> allSumPosts = FastList.newList((int)totalPosts);

        DBCommand cmdSelect = db.createCommand();
        cmdSelect.select(db.T_POSTS.C_POST_ID,
                db.T_POSTS.C_POSTER_ID,
                db.T_POSTS.C_TITLE,
                db.T_POSTS.C_DATE_POSTED
        );
        cmdSelect.orderBy(db.T_POSTS.C_DATE_POSTED, true);

        DBReader reader = new DBReader();
        try {
            reader.open(cmdSelect, con);
            allSumPosts.addAll(0, reader.getBeanList(SummaryPostBean.class));
        }
        catch (Exception e) {
            allSumPosts.clear();
        }
        finally {
            reader.close();
        }

        return allSumPosts;
    }
}
