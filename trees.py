import math;
import operator;

def calcShannonEnt (dataset) :
    numEntries = len(dataset)
    labelCounts = {}
    for featVec in dataset :
        currentLabel = featVec[-1]
        if currentLabel not  in labelCounts.keys() :
            labelCounts[currentLabel] = 0
        labelCounts[currentLabel] += 1
    shannonEnt = 0.0
    for key in labelCounts.keys():
        prob = float(labelCounts[key]) / numEntries
        shannonEnt -= prob * math.log(prob, 2)
    return shannonEnt

def createDataset() :
    dataset = [[1, 1, 'yes'],
               [1, 1, 'yes'],
               [1, 0, 'no'],
               [0, 1, 'no'],
               [0, 1, 'no']]
    labels = ['no surfacing', 'flipper']
    return dataset, labels

def splitDataSet(dataset, axis, value) :
    retDataset = []
    for featVec in dataset :
        if featVec[axis] == value:
            reducedFeatVec = featVec[0 : axis]
            reducedFeatVec.extend(featVec[axis + 1 : ])
            retDataset.append(reducedFeatVec)
    return retDataset


def chooseBestFeatureToSplit(dataset) :
    numFeatures = len(dataset[0]) - 1
    baseEntropy = calcShannonEnt(dataset)
    bestInfoGain = 0.0
    bestFeature = -1
    for i in range(numFeatures) :
        featureList = [example[i] for example in dataset]
        uniqueValues = set(featureList)
        newEntropy = 0.0
        for value in uniqueValues :
            subDataSet = splitDataSet(dataset, i, value)
            prob = len(subDataSet) / float(len(dataset))
            newEntropy += prob * calcShannonEnt(subDataSet)

        infoGain = baseEntropy - newEntropy
        if infoGain > bestInfoGain :
            bestInfoGain = infoGain
            bestFeature = i

    return bestFeature

def majorityCnt(classLst) :
    classCnt = {}
    for vote in classLst :
        if vote not in classCnt.keys() :
            classCnt[vote] = 0
        classCnt[vote] += 1
    sortedClassCnt = sorted (classCnt, key = operator.itemgetter(1), reverse = True)
    return sortedClassCnt[0][0]

def createTree (dataset, labels) :
    classLst = [example[-1] for example in dataset]
    if classLst.count(classLst[0]) == len(classLst) :
        return classLst[0]
    if len(dataset[0]) == 1 :
        return majorityCnt(classLst)
    bestFeature = chooseBestFeatureToSplit(dataset)
    bestFeatureLabel = labels[bestFeature]
    myTree = {bestFeatureLabel : {}}
    del(labels[bestFeature])
    featureValues = [example[bestFeature] for example in dataset]
    uniqueValues = set (featureValues)
    for value in uniqueValues :
        subLabels = labels[ : ]
        myTree[bestFeatureLabel][value] = createTree(splitDataSet(dataset, bestFeature, value), subLabels)

    return myTree



if __name__ == '__main__' :
    dataset, labels = createDataset()
    #shannonEnt = calcShannonEnt(dataset)
    #print(shannonEnt)
    #retDataset = splitDataSet(dataset, 0, 0)
    mytree = createTree(dataset, labels)
    print(mytree)