package com.vv.minerlamp.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Restrictions;

import com.vv.minerlamp.entity.LampUnit;
import com.vv.minerlamp.entity.RackStatistics;
import com.vv.minerlamp.util.HibernateUtil;
import com.vv.minerlamp.util.StaffAction;
import com.vv.minerlamp.util.StaffState;

public class LampUnitDAO {

	public List<LampUnit> getUnitsByRackId(Long rackId) {
		List<LampUnit> list = new ArrayList<LampUnit>();
		Session session = HibernateUtil.getSessionFactory().openSession();
		list = session.createQuery(
				"from LampUnit as unit where unit.rackId=" + rackId
						+ " order by unit.lampNo asc").list();
		return list;
	}

	public List<LampUnit> listAllForModel() {
		List<LampUnit> list = new ArrayList<LampUnit>();
		Session session = HibernateUtil.getSessionFactory().openSession();
		;
		list = session
				.createQuery(
						"select unit.rackId,unit.lampNo,unit.chargingCounts,unit.chargingBeginTime,unit.manufacturer from LampUnit as unit")
				.list();
		return list;
	}

	public void batchSave(int rackCount, int unitCount) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			Query query = session.createQuery("delete from LampUnit");
			query.executeUpdate();
			for (int i = 1; i <= rackCount; i++) {
				for (int j = 1; j <= unitCount; j++) {
					LampUnit lampUnit = new LampUnit();
					lampUnit.setRackId(new Long(i));
					lampUnit.setLampNo(new Long(j));
					lampUnit.setName("" + i + "-" + j);
					lampUnit.setState(StaffState.STAFF_UNDEFINED);
					
					session.save(lampUnit);
				}
			}
			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	public void batchSaveInCurrentSession(int rackCount, int unitCount,
			Session session) {

		Query query = session.createQuery("delete from LampUnit");
		query.executeUpdate();
		for (int i = 1; i <= rackCount; i++) {
			for (int j = 1; j <= unitCount; j++) {
				LampUnit lampUnit = new LampUnit();
				lampUnit.setRackId(new Long(i));
				lampUnit.setLampNo(new Long(j));
				lampUnit.setName("" + i + "-" + j);
				lampUnit.setState(StaffState.STAFF_UNDEFINED);
				lampUnit.setChargingCounts(0);
				// lampUnit.setChargingBeginTime(new Date());
				// lampUnit.setChargingLastTime(new Date());
				session.save(lampUnit);
			}
		}

	}

	public void changeLamp(Long rackId, Long lampNo, int chargingCounts,
			Date chargingBeginTime) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			Query query = session
					.createQuery("update LampUnit set chargingCounts=:chargingCounts , chargingBeginTime=:chargingBeginTime  where rackId=:rackId and lampNo=:lampNo");
			query.setInteger("chargingCounts", chargingCounts);
			query.setTimestamp("chargingBeginTime", chargingBeginTime);

			query.setLong("rackId", rackId);
			query.setLong("lampNo", lampNo);
			query.executeUpdate();

			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	public void setLampStateInCurrentSession(Long rackId, Long lampNo,
			Integer oldState, Integer action, String manufacturer,
			Session session) {
		String sql = "update LampUnit set state=:state,manufacturer=:manufacturer #1 where rackId=:rackId and lampNo=:lampNo";
		Date chargingBeginTime = null;
		Integer state = oldState;
		if (action == StaffAction.DEFINE_STAFF) {
			state = StaffState.STAFF_DEFINED;
			chargingBeginTime = new Date();
			sql = sql.replace("#1", ",chargingBeginTime=:chargingBeginTime");
		} else if (action == StaffAction.DELETE_STAFF) {
			state = StaffState.STAFF_UNDEFINED;
			sql = sql.replace("#1", ",chargingBeginTime=:chargingBeginTime");
		} else {
			sql = sql.replace("#1", "");
		}
		Query query = session.createQuery(sql);
		query.setInteger("state", state);
		query.setString("manufacturer", manufacturer);
		query.setLong("rackId", rackId);
		query.setLong("lampNo", lampNo);
		if (action == StaffAction.DEFINE_STAFF
				|| action == StaffAction.DELETE_STAFF) {
			query.setDate("chargingBeginTime", chargingBeginTime);
		}
		query.executeUpdate();

	}

	public LampUnit queryByLampNoandRackId(Long lampNo, Long rackId) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		LampUnit result;

		LampUnit lampUnit = new LampUnit();
		lampUnit.setLampNo(lampNo);
		lampUnit.setRackId(rackId);
		Criteria criteria = session.createCriteria(LampUnit.class);
		criteria.add(Example.create(lampUnit));
		result = (LampUnit) criteria.uniqueResult();
		session.close();
		return result;
	}

	public List<LampUnit> queryByStateAndRackId(Integer state, Long rackId,
			boolean isState) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<LampUnit> result = new ArrayList<LampUnit>();

		Criteria criteria = session.createCriteria(LampUnit.class);
		criteria.add(Restrictions.eq("rackId", rackId));
		if (isState) {
			criteria.add(Restrictions.eq("state", state));
		} else {
			criteria.add(Restrictions.ne("state", state));
		}
		result = criteria.list();
		session.close();
		return result;
	}

	public void updateLampUnitStateAndChargingCountIfNecessary(Integer state,
			Long rackId, Long lampNo) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			StringBuilder hql = new StringBuilder(
					"update LampUnit set state=:state ");
			if (state == StaffState.LAMP_CHARGING) {
				hql.append(",chargingCounts=chargingCounts+1");
			}
			hql.append("  where rackId=:rackId and lampNo=:lampNo");
			Query query = session.createQuery(hql.toString());
			query.setInteger("state", state);
			query.setLong("rackId", rackId);
			query.setLong("lampNo", lampNo);
			query.executeUpdate();
			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	public void queryRackStatisticsModel(Long rackId,
			RackStatistics totalRackStatistics, RackStatistics rackStatistics) {
		Session session = HibernateUtil.getSessionFactory().openSession();

		rackStatistics.setId(rackId);
		String hql = "SELECT lampUnit.rackId,lampUnit.state,COUNT(*) FROM LampUnit lampUnit  GROUP BY lampUnit.rackId,lampUnit.state ";

		List<Object[]> resultList;
		Query query = session.createQuery(hql);
		resultList = query.list();

		if (resultList != null && resultList.size() > 0) {

			for (int i = 0; i < resultList.size(); i++) {
				Object[] statistics = (Object[]) resultList.get(i);

				if (statistics[1].equals(StaffState.STAFF_UNDEFINED)) {
					totalRackStatistics.addIdleCount((Long) (statistics[2]));
					if (rackId.equals(statistics[0])) {
						rackStatistics.setIdleCount((Long) (statistics[2]));
					}
				} else if (statistics[1].equals(StaffState.STAFF_UNDERGROUND)||statistics[1].equals(StaffState.STAFF_DEFINED)) {
					totalRackStatistics
							.addUndergroundCount((Long) (statistics[2]));
					if (rackId.equals(statistics[0])) {
						rackStatistics
								.addUndergroundCount((Long) (statistics[2]));
					}
				} else if (statistics[1].equals(StaffState.LAMP_CHARGING)) {
					totalRackStatistics
							.addChargingCount((Long) (statistics[2]));
					if (rackId.equals(statistics[0])) {
						rackStatistics.addChargingCount((Long) (statistics[2]));
					}
				} else if (statistics[1].equals(StaffState.LAMP_FULL)) {
					totalRackStatistics.addFullCount((Long) (statistics[2]));
					if (rackId.equals(statistics[0])) {
						rackStatistics.addFullCount((Long) (statistics[2]));
					}
				} else if (statistics[1].equals(StaffState.LAMP_ERROR)) {
					totalRackStatistics.addErrorCount((Long) (statistics[2]));
					if (rackId.equals(statistics[0])) {
						rackStatistics.addErrorCount((Long) (statistics[2]));
					}
				}

			}
		}

		session.close();

	}
}
