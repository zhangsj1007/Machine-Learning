package com.chris.kpi_monitor.monitor.algorithm;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.stylesheets.LinkStyle;

import com.chris.kpi_monitor.data.CorporateName;


public class DBScanPath{
		//Link
		public class DBScanLink{
			public DBScanLink(int linkId , int length) {
				_linkId = linkId;
				_length = length;
			}
			public int getLinkId() {
				return _linkId;
			}
			public void setLinkId(int linkId) {
				_linkId = linkId;
			}
			public int getLength() {
				return _length;
			}
			public void setLength(int length) {
				_length = length;
			}
			private int _linkId;
			private int _length;
		}
		
		public DBScanPath(CorporateName corporateName, int indexInOrigin) {
			_links = new ArrayList<DBScanLink>();
			_corporateName = corporateName;
			_indexInOrigin = indexInOrigin;
		}
		
		int getTotalLengths() {
			if (_sumLengths > -1) {
				return _sumLengths;
			}
			_sumLengths = 0;
			for (DBScanLink link : _links) {
				_sumLengths += link.getLength();
			}
			return _sumLengths;
		}
		
		public boolean isVisited() {
			return _visited;
		}
		
		public void setVisited(boolean visited) {
			_visited = visited;
		}
		
		public boolean isNoise() {
			return _noise;
		}
		
		public void setNoise(boolean noise) {
			_noise = noise;
		}
		
		public int getCluster() {
			return _cluster;
		}
		
		public void setCluster(int cluster) {
			_cluster = cluster;
		}
		
		public void addDBScanLink(DBScanLink link) {
			_links.add(link);
		}
		
		public List<DBScanLink> getLinks(){
			return _links;
		}
		
		public CorporateName getCorporateName() {
			return _corporateName;
		}
		
		public void setCorporateName(CorporateName corporateName) {
			_corporateName = corporateName;
		}
		
		public int getindexInOrigin() {
			return _indexInOrigin;
		}
		
		public void setIndexInOrigin(int indexInOrigin) {
			_indexInOrigin = indexInOrigin;
		}
		
		private boolean _visited = false;
		private boolean _noise = false;
		private int _sumLengths = -1;
		private int _cluster = -1;
		private List<DBScanLink> _links;
		private CorporateName _corporateName;
		private int _indexInOrigin;
	}
