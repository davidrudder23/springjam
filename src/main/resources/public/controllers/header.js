function Header($scope, $http, $rootScope) {

    $http.get('/api/user')
        .success(function (data) {
            $scope.user = data;
            $scope.loggedIn = true;
            $rootScope.loggedIn = true;
        })
        .error(function (data) {
            $scope.loggedIn = false;
        })
    ;

    $http.get('/api/band')
        .success(function (data) {
            $scope.bands = data;

            bandObj = localStorage.getItem("selectedBand");

            if (angular.isUndefined(bandObj) || bandObj==null) {
                localStorage.setItem("selectedBand", JSON.stringify($scope.bands[0]));
                bandObj = JSON.stringify($scope.bands[0]);
            }

            $rootScope.selectedBand = JSON.parse(bandObj);
        })
        .error(function(data) {
            $rootScope.selectedBand = "";
        });


    $rootScope.selectBand = function(band) {
        localStorage.setItem("selectedBand", JSON.stringify(band));
        console.log("Setting selected band 3");
        $rootScope.selectedBand = band;

    };
}
