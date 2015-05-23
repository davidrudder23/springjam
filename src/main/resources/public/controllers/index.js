function Band($scope, $http) {
    $http.get('/api/band').
        success(function(data) {
            $scope.oldbands = data;
        }).then(function() {
            $http.get('/band/twiddle').
                success(function(data) {
                    $scope.twiddle = data;
                });
        });
}