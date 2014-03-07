package com.vv.minerlamp.dao;


public class RackStatisticsDAO {
	public RackStatisticsDAO() {

	}

	// public void update(Long rackId, Integer oldState, Integer action) {
	//
	// Session session = HibernateUtil.getSessionFactory().openSession();
	// Transaction transaction = null;
	// try {
	// transaction = session.beginTransaction();
	//
	// if (SysConfiguration.rackStatisticsList.get(rackId.intValue()) == null) {
	// SysConfiguration.rackStatisticsList.set(rackId.intValue(),
	// (RackStatistics) session.get(RackStatistics.class,
	// rackId));
	// }
	// if (SysConfiguration.rackStatisticsList.get(0) == null) {
	// SysConfiguration.rackStatisticsList.set(0,
	// (RackStatistics) session.get(RackStatistics.class, 0));
	// }
	// switch (action) {
	// case StaffAction.DEFINE_STAFF:
	// SysConfiguration.rackStatisticsList.get(rackId.intValue())
	// .minusIdleCount();
	// SysConfiguration.rackStatisticsList.get(0).minusIdleCount();
	// break;
	// case StaffAction.DELETE_STAFF:
	// SysConfiguration.rackStatisticsList.get(rackId.intValue())
	// .addIdleCount();
	// SysConfiguration.rackStatisticsList.get(0).addIdleCount();
	// break;
	// case StaffAction.TAKE_AWAY:
	// SysConfiguration.rackStatisticsList.get(rackId.intValue())
	// .minusChargingCount();
	// SysConfiguration.rackStatisticsList.get(rackId.intValue())
	// .addUndergroundCount();
	//
	// SysConfiguration.rackStatisticsList.get(0).minusChargingCount();
	// SysConfiguration.rackStatisticsList.get(0)
	// .addUndergroundCount();
	// break;
	// case StaffAction.PUT_ON:
	// SysConfiguration.rackStatisticsList.get(rackId.intValue())
	// .addChargingCount();
	// SysConfiguration.rackStatisticsList.get(rackId.intValue())
	// .minusUndergroundCount();
	//
	// SysConfiguration.rackStatisticsList.get(0).addChargingCount();
	// SysConfiguration.rackStatisticsList.get(0)
	// .minusUndergroundCount();
	// break;
	// case StaffAction.CHARGING_OK:
	// SysConfiguration.rackStatisticsList.get(rackId.intValue())
	// .minusChargingCount();
	// SysConfiguration.rackStatisticsList.get(rackId.intValue())
	// .addFullCount();
	//
	// SysConfiguration.rackStatisticsList.get(0).minusChargingCount();
	// SysConfiguration.rackStatisticsList.get(0).addFullCount();
	// case StaffAction.ERROR_HAPPENS:
	// SysConfiguration.rackStatisticsList.get(rackId.intValue())
	// .addErrorCount();
	//
	// SysConfiguration.rackStatisticsList.get(0).addErrorCount();
	// break;
	// case StaffAction.ERROR_REPARED:
	// SysConfiguration.rackStatisticsList.get(rackId.intValue())
	// .minusChargingCount();
	//
	// SysConfiguration.rackStatisticsList.get(0).minusChargingCount();
	// break;
	// default:
	// break;
	// }
	// session.update(SysConfiguration.rackStatisticsList.get(rackId
	// .intValue()));
	// session.update(SysConfiguration.rackStatisticsList.get(0));
	// transaction.commit();
	// } catch (HibernateException e) {
	// transaction.rollback();
	// e.printStackTrace();
	// } finally {
	// session.close();
	// }
	// }

}
