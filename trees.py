import math;

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
            splitDataSet = splitDataSet(dataset, i, value)
            prob = len(splitDataSet) / float(len(dataset))
            newEntropy += prob * calcShannonEnt(splitDataSet)

        infoGain = baseEntropy - newEntropy
        if infoGain > bestInfoGain :
            bestInfoGain = infoGain
            bestFeature = i

    return bestFeature

if __name__ == '__main__' :
    dataset, labels = createDataset()
    #shannonEnt = calcShannonEnt(dataset)
    #print(shannonEnt)
    retDataset = splitDataSet(dataset, 0, 0)
    print(retDataset)