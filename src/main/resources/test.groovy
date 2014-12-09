
/*
 * results is fed into this script
 * this result list is a list of maps where each list item is a row,  and the map represents a key value pair of key=column name and value = value
 */



query = """SELECT me.entity_name  AS name,
  sysman.em_metric_value_access(val.met_values,9)                                     AS tpm,
  sysman.em_metric_value_access(val.met_values,10)                                    AS epm,
  sysman.em_metric_value_access(val.met_values,6)                                     AS rt ,
  REGEXP_REPLACE(parents.display_name,'_osb_server.*','')                             AS pop,
  parents.display_name                                                                AS host,
  ROUND( ((val.collection_time - TO_DATE('1970-01-01', 'YYYY-MM-DD') )* 86400 )) AS TIME
FROM sysman.em_metric_groups grps,
  sysman.em_metric_items itms,
  sysman.em_metric_values val,
  sysman.gc_manageable_entities me,
  SYSMAN.gc_manageable_entities parents
WHERE grps.metric_group_id = 763
AND grps.metric_group_id   = itms.metric_group_id
AND val.metric_item_id     = itms.metric_item_id
AND me.entity_guid         = itms.target_guid
AND parents.entity_guid    = me.parent_me_guid
AND val.collection_time    between (sysdate - ${(System.currentTimeMillis() - lastRun)/86400000}) and (sysdate)"""


86400000
jdbcUrl = 'jdbc:oracle:thin:@10.13.81.102:1521/emrep.ds.dtvops.net'
jdbcDriver = 'oracle.jdbc.driver.OracleDriver'
pathForJar = 'jar:file:/users/srstone/workspacenewnew/sql2carbonorstatsd/src/test/resources/ojdbc11-11.2.0.3.jar!/'
dbUser = 'sysman_ro'
dbPassword = 'sysman_ro'
/**
 * the date of the previous run will be passed to the script
 */
lastRun = new Date();



mapping = [UPDA:".*UPDA.*",AuthenticationService:".*AS.*",DPS:".*DeviceProfileService.*",SharedService:".*Shared-Services.*"]
/**
 * this is a closure where a row will be passed in for your query
 */
rowprocessor = { row ->
	String appr = "Other"

	for(i in mapping){
		if (row.NAME.matches(i.value)){
			appr = i.key
			break;
		}
	}


	def name = "${row.NAME}.${row.POP}.${row.HOST}".replaceAll(' ',"_");

	/**
	 * return a list of metrics.  if only returning on still wrap it in [] as we are expecting a list
	 */
	[
		new Metric(app:appr,resource:name,name:'tpm',value:row.TPM,timestamp:row.TIME,pop:row.POP,host:row.HOST),
		new Metric(app:appr,resource:name,name:'epm',value:row.EPM,timestamp:row.TIME,pop:row.POP,host:row.HOST),
		new Metric(app:appr,resource:name,name:'rt',value:row.RT,timestamp:row.TIME,pop:row.POP,host:row.HOST)
	]
}
