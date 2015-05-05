function Band($scope, $http) {
    $http.get('/band').
        success(function(data) {
            $scope.bands = data;
        }).then(function() {
            $http.get('/band/twiddle').
                success(function(data) {
                    $scope.twiddle = data;
                });
        });
}