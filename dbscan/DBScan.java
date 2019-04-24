package com.chris.kpi_monitor.monitor.algorithm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.chris.kpi_monitor.monitor.algorithm.DBScanPath.DBScanLink;
 
public class DBScan {
	private static double _calDistance(DBScanPath path1, DBScanPath path2) {
		Set<Integer> linkSet = new HashSet<Integer>();
		int commonLenths = 0;
		List<DBScanLink> path1Links = path1.getLinks();
		List<DBScanLink> path2Links = path2.getLinks();
		for (DBScanLink link : path1Links) {
			linkSet.add(link.getLinkId());
		}
		for (DBScanLink link : path2Links) {
			if (linkSet.contains(link.getLinkId())) {
				commonLenths += link.getLength();
			}
		}
		
		return 1 - Double.min((double)commonLenths / path1.getTotalLengths(), (double)commonLenths / path2.getTotalLengths());
	}
    
	public static void cluster(double eps, int minPaths, List<DBScanPath> paths){
		int cluster = 0;
		for (DBScanPath path : paths) {
			if (path.isVisited()) {
				continue;
			}
			path.setVisited(true);
			List<DBScanPath> neighPaths = _regionQuery(eps, path, paths);
			if (neighPaths.size() < minPaths) {
				path.setNoise(true);
			}else {
				path.setCluster(cluster);
				for (int i = 0; i < neighPaths.size(); i++) {
					DBScanPath neighPath = neighPaths.get(i);
					if (!neighPath.isVisited()) {
						neighPath.setVisited(true);
						List<DBScanPath> neighNeighPaths = _regionQuery(eps, neighPath, paths);
						if (neighNeighPaths.size() >= minPaths) {
							neighPaths.addAll(neighNeighPaths);
						}
					}
					
					if (neighPath.getCluster() == -1) {
						neighPath.setCluster(cluster);
					}
					if (neighPath.isNoise()) {
						neighPath.setNoise(false);
					}
				}
				cluster++;
			}
		}
	}
	
	private static List<DBScanPath> _regionQuery(double eps, DBScanPath corePath, List<DBScanPath> paths){
		List<DBScanPath> regionPaths = new ArrayList<DBScanPath>();
		for (DBScanPath neighPath : paths) {
			double d = _calDistance(corePath, neighPath);
			if (d < eps) {
				regionPaths.add(neighPath);
			}
		}
		return regionPaths;
	}
}
