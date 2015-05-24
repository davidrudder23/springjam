function Index($scope, $http) {

    $http.get("/api/band")
        .success(function(data){
            $scope.bands=data;
        })
        .then(function(data){
            angular.forEach($scope.bands, function (band, bandsId) {
                $http.get("/api/concert/"+band.id+"/seen")
                    .success(function(data){
                       band.seenConcerts = data;
                    });
            });
        })
    ;
}