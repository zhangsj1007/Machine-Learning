# -*- coding: utf-8 -*-

import numpy as np
import math

class S_Dbw(object) :

    '''
    @:param data : np.array， n维的原始数据
    @:param dataclusterIds : np.array， data中每一个数据的聚类clusterId
    @:param centerIdxs : None或np.array，cluster center entry index， 对于
    k-means等聚类算法，能够算出来center entry，则直接输入；对于dbscan等算法，算法本身不能
    够算出来center entry，则输入None，算法中会找到一个cluster中和mean entry最近的entry作为
    center entry。
    '''

    def __init__(self, data, dataClusterIds, centerIdxs = None):
        self.data = data
        self.dataClusterIds = dataClusterIds

        if centerIdxs is not None :
            self.centerIdxs = centerIdxs
        else :
            self.__getCenterIdxs()

        #self.centerIdxs = centerIdxs

        self.clusterNum = len(self.centerIdxs)

        self.stdev = self.__getStdev()

        self.clusterDensity = []

        for i in range(self.clusterNum):
            self.clusterDensity.append(self.__density(self.data[self.centerIdxs[i]],i))

    #计算center index
    def __getCenterIdxs(self) :

        self.centerIdxs = []

        clusterDataMp = {}
        clusterDataIdxsMp = {}

        for i in range(len(self.data)) :
            entry = self.data[i]
            clusterId = self.dataClusterIds[i]
            clusterDataMp.setdefault(clusterId, []).append(entry)
            clusterDataIdxsMp.setdefault(clusterId, []).append(i)

        for clusterId in sorted(clusterDataMp.keys()) :
            subData = clusterDataMp[clusterId]
            subDataIdxs = clusterDataIdxsMp[clusterId]

            m = len(subData[0])
            n = len(subData)

            meanEntry = [0.0] * m

            for entry in subData :
                meanEntry += entry

            meanEntry = meanEntry / n

            minDist = float("inf")

            centerIdx = 0

            for i in range(len(subData)) :
                entry = subData[i]
                idx = subDataIdxs[i]
                dist = self.__dist(entry, meanEntry)
                if minDist > dist:
                    centerIdx = idx
                    minDist = dist

            self.centerIdxs.append(centerIdx)

    def __getStdev(self) :
        stdev = 0.0

        for i in range(self.clusterNum) :
            varMatrix = np.var(self.data[self.dataClusterIds == i], axis=0)
            stdev += math.sqrt(np.dot(varMatrix.T, varMatrix))

        stdev = math.sqrt(stdev) / self.clusterNum

        return stdev

    def __density(self, center, clusterIdx):

        density = 0

        clusterData = self.data[self.dataClusterIds == clusterIdx]
        for i in clusterData :
            if self.__dist(i, center) <= self.stdev:
                density += 1

        return density

    def __Dens_bw(self):

        variance = 0

        for i in range(self.clusterNum):
            for j in range(self.clusterNum):
                if i == j:
                    continue
                center = self.data[self.centerIdxs[i]] + self.data[self.centerIdxs[j]] / 2
                interDensity = self.__density(center, i) + self.__density(center, j)
                variance += interDensity / max(self.clusterDensity[i], self.clusterDensity[j])

        return variance / (self.clusterNum * (self.clusterNum - 1))

    def __Scater(self):
        thetaC = np.var(self.data, axis=0)
        thetaCNorm = math.sqrt(np.dot(thetaC.T, thetaC))

        thetaSumNorm = 0

        for i in range(self.clusterNum):
            clusterData = self.data[self.dataClusterIds == i]
            theta_ = np.var(clusterData, axis=0)
            thetaNorm_ = math.sqrt(np.dot(theta_.T, theta_))
            thetaSumNorm += thetaNorm_

        return (1 / self.clusterNum) * (thetaSumNorm / thetaCNorm)

    #计算data entry的欧拉距离
    def __dist(self, entry1, entry2):
        return np.linalg.norm(entry1 - entry2)

    def result(self):
        return self.__Dens_bw() + self.__Scater()



data = np.array([[1, 1, 1], [0, 0, 0], [1, 1, 2], [2, 2, 2], [2,2,3]])
data_cluster = np.array([1, 0, 1, 2, 2])
centers_index = np.array([1, 0, 3])

a = S_Dbw(data, data_cluster, centers_index)
#a = S_Dbw(data, data_cluster)
print(a.result())






