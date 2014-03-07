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
import com.vv.minerlamp.entity.Staff;
import com.vv.minerlamp.util.HibernateUtil;
import com.vv.minerlamp.util.StaffAction;
import com.vv.minerlamp.util.StaffState;
import com.vv.minerlamp.util.SysConfiguration;

public class LampUnitDAO {

	public List<LampUnit> getUnitsByRackId(Long rackId) {
		List<LampUnit> list = new ArrayList<LampUnit>();
		Session session = HibernateUtil.getSessionFactory().openSession();
		list = session.createQuery(
				"from LampUnit as unit where unit.rackId=" + rackId
						+ " order by unit.lampNo asc").list();
		return list;
	}

	public List<LampUnit> listAllForModel(boolean useLimit) {
		List<LampUnit> list = new ArrayList<LampUnit>();
		Session session = HibernateUtil.getSessionFactory().openSession();
		String hql = "select unit.rackId,unit.lampNo,unit.chargingCounts,unit.chargingBeginTime,unit.chargingLastTime,unit.chargingTotalTime,unit.manufacturer from LampUnit as unit ";
		if (useLimit == true) {
			if (SysConfiguration.isCountLimited == SysConfiguration.COUNT_LIMIT) {
				hql += " where unit.chargingCounts>="
						+ SysConfiguration.limitCount;
			} else {
				// TODO充电时间累计超时

			}
		}
		list = session.createQuery(hql).list();
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
					lampUnit.setChargingCounts(0);
					lampUnit.setChargingTotalTime(0L);
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
				lampUnit.setChargingTotalTime(0L);
				lampUnit.setStaffName("姓名");
				lampUnit.setStaffDepart("部门");
				lampUnit.setStaffWorkType("工种");
				// lampUnit.setChargingBeginTime(new Date());
				// lampUnit.setChargingLastTime(new Date());
				session.save(lampUnit);
			}
		}

	}

	public void batchAddSaveInCurrentSession(Long maxRackId, int addRackCount,
			int unitCount, Session session) {
		int lastRackId = 0;
		if (maxRackId != null) {
			lastRackId = maxRackId.intValue();
		}

		for (int i = lastRackId + 1; i <= lastRackId + addRackCount; i++) {
			for (int j = 1; j <= unitCount; j++) {
				LampUnit lampUnit = new LampUnit();
				lampUnit.setRackId(new Long(i));
				lampUnit.setLampNo(new Long(j));
				lampUnit.setName("" + i + "-" + j);
				lampUnit.setState(StaffState.STAFF_UNDEFINED);
				lampUnit.setChargingCounts(0);
				lampUnit.setChargingTotalTime(0L);
				lampUnit.setStaffName("姓名");
				lampUnit.setStaffDepart("部门");
				lampUnit.setStaffWorkType("工种");
				// lampUnit.setChargingBeginTime(new Date());
				// lampUnit.setChargingLastTime(new Date());
				session.save(lampUnit);
			}
		}

	}
	public void changeLampUseSta(Long rackId, Long lampNo, int chargingCounts,
			Long chargingBeginTime) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			Query query = session
					.createQuery("update LampUnit set chargingCounts=:chargingCounts , chargingBeginTime=:chargingBeginTime  where rackId=:rackId and lampNo=:lampNo");
			query.setInteger("chargingCounts", chargingCounts);
			query.setLong("chargingBeginTime", chargingBeginTime);
	
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
	public void changeLamp(Long rackId, Long lampNo, int chargingCounts,
			Long chargingBeginTime) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			Query query = session
					.createQuery("update LampUnit set chargingCounts=:chargingCounts , chargingBeginTime=:chargingBeginTime,chargingLastTime=:chargingLastTime,chargingTotalTime=:chargingTotalTime  where rackId=:rackId and lampNo=:lampNo");
			query.setInteger("chargingCounts", chargingCounts);
			query.setLong("chargingBeginTime", chargingBeginTime);
			query.setLong("chargingLastTime",0 );
			query.setLong("chargingTotalTime", 0);
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

	public void setLampStateInCurrentSession(Long rackId, Long lampNo,Staff staff,
			Integer oldState, Integer action, String manufacturer,
			Session session) {
		String sql = "update LampUnit set state=:state,staffName=:staffName,staffDepart=:staffDepart,staffWorkType=:staffWorkType,manufacturer=:manufacturer #1 where rackId=:rackId and lampNo=:lampNo";
		String staffName="姓名";
		String staffDepart="部门";
		String staffWorkType="工种";
		Long chargingBeginTime = 0L;
		Integer state = oldState;
		if(staff!=null){
			staffName=staff.getName();
			staffDepart=staff.getDepartment();
			staffWorkType=staff.getProfession();
		}
		if (action == StaffAction.DEFINE_STAFF) {
			
			state = StaffState.STAFF_DEFINED;
			chargingBeginTime = new Date().getTime();
			sql = sql.replace("#1", ",chargingBeginTime=:chargingBeginTime,chargingLastTime=:chargingLastTime,chargingTotalTime=:chargingTotalTime,chargingCounts=:chargingCounts");
		} else if (action == StaffAction.DELETE_STAFF) {
			state = StaffState.STAFF_UNDEFINED;
			sql = sql.replace("#1", ",chargingBeginTime=:chargingBeginTime,chargingLastTime=:chargingLastTime,chargingTotalTime=:chargingTotalTime,chargingCounts=:chargingCounts");
		} else {
			sql = sql.replace("#1", "");
		}
		Query query = session.createQuery(sql);
		query.setInteger("state", state);
		query.setString("manufacturer", manufacturer);
		query.setLong("rackId", rackId);
		query.setLong("lampNo", lampNo);
		query.setString("staffName", staffName);
		query.setString("staffDepart", staffDepart);
		query.setString("staffWorkType",staffWorkType );
		
		if (action == StaffAction.DEFINE_STAFF
				|| action == StaffAction.DELETE_STAFF) {
			query.setLong("chargingBeginTime", chargingBeginTime);
			query.setLong("chargingLastTime",0 );
			query.setLong("chargingTotalTime", 0);
			query.setInteger("chargingCounts", 0);
			
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

	public void updateLampUnitStateAndChargingCountIfNecessary(
			LampUnit lampUnit, Integer state, Long rackId, Long lampNo) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		Integer oldState = null;
		try {
			if (lampUnit != null) {
				oldState = lampUnit.getState();
			}
			transaction = session.beginTransaction();
			StringBuilder hql = new StringBuilder(
					"update LampUnit set state=:state ");
			if (state == StaffState.LAMP_CHARGING
					|| state == StaffState.LAMP_FULL) {
				if (oldState == null
						|| oldState == StaffState.STAFF_UNDERGROUND
						|| oldState == StaffState.STAFF_DEFINED
						|| oldState == StaffState.LAMP_OFFLINE
						|| oldState == StaffState.LAMP_ERROR) {
					hql.append(",chargingCounts=chargingCounts+1");
					hql.append(",chargingLastTime=" +new Date().getTime());
				}
			}
			System.out.println("oldState="+oldState+" state="+state);
			if ((oldState!=null)&&(oldState == StaffState.LAMP_CHARGING
					|| oldState == StaffState.LAMP_FULL)) {
				if (state == null || state == StaffState.STAFF_UNDERGROUND
						|| state == StaffState.LAMP_OFFLINE
						|| oldState == StaffState.LAMP_ERROR) {
					System.out.println("更新累计充电时长");
					hql.append(",chargingTotalTime=chargingTotalTime+" + ((new Date().getTime()-lampUnit.getChargingLastTime())/1000+59)/60);
				}

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
				} else if (statistics[1].equals(StaffState.STAFF_UNDERGROUND)
						|| statistics[1].equals(StaffState.STAFF_DEFINED)) {
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
