package com.vv.minerlamp.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.vv.minerlamp.entity.LampRack;
import com.vv.minerlamp.util.HibernateUtil;

public class LampRackDAO {
	public List<LampRack> getAll() {
		List list = new ArrayList<LampRack>();
		Session session = HibernateUtil.getSessionFactory().openSession();
		String hql="from LampRack order by id asc";
		list = session.createQuery(hql).list();
		return list;
	}

	public void batchSave(int rackCount) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		try {
			Query query = session.createQuery("delete from LampPack");
			query.executeUpdate();
			for (int i = 1; i <= rackCount; i++) {
				LampRack lampRack = new LampRack();
				lampRack.setId(new Long(i));
				lampRack.setName("µÆ¼Ü" + i);
				session.save(lampRack);
			}
			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}


	public void batchSaveInCurrentSession(int rackCount, Session session) {

		Query query = session.createQuery("delete from LampRack");
		query.executeUpdate();
		for (int i = 1; i <= rackCount; i++) {
			LampRack lampRack = new LampRack();
			lampRack.setId(new Long(i));
			lampRack.setName("µÆ¼Ü" + i);
			session.save(lampRack);
		}

	}
	public Long getMaxIdInCurrentSession(Session session){
		Query query = session.createQuery("select max(id) from LampRack");
		Long maxId = (Long) query.uniqueResult();
		return maxId;
	}
	public void batchAddSaveInCurrentSession(Long maxId,int addRackCount, Session session) {
		int lastId=0;
		if(maxId!=null){
			lastId=maxId.intValue();
		}
		for (int i = lastId + 1; i <= lastId + addRackCount; i++) {
			LampRack lampRack = new LampRack();
			lampRack.setId(new Long(i));
			lampRack.setName("µÆ¼Ü" + i);
			session.save(lampRack);
		}

	}

}
